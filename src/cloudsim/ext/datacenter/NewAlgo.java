package cloudsim.ext.datacenter;

import java.util.*;

import cloudsim.ext.Constants;
import cloudsim.ext.event.CloudSimEvent;
import cloudsim.ext.event.CloudSimEventListener;
import cloudsim.ext.event.CloudSimEvents;
import gridsim.GridSim;
class PP implements Comparable<PP>{

    private int index;
    private double power;

    public PP(int index,double power)
    {
        super();
        this.index = index;
        this.power = power;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }


    @Override
    public int compareTo(PP o) {
        double comparePower = ((PP)o).getPower();
        return Math.toIntExact((long) ((comparePower - this.power )*1000000));
    }
}
public class NewAlgo  extends VmLoadBalancer implements CloudSimEventListener  {

    private Map<Integer, VirtualMachineState> vmStatesList;
    private Map<Integer, Integer> currentAllocationCounts;
    private int flag = 0;
    int init = 0;
    double storage[] ;
    double sum[] ;
    int counter[];
    double first[];
    double second[];
    int f[];
    int s[];
    PP p[] ;
    int begin = 0;
    int choice = 1;
    int currVm = -1;
    int vmId = -1;
    int size;
    int clusterCount = 0;
    int startBalancer  = 0;

    ArrayList c1 ;

    ArrayList c2 ;

    ArrayList c3 ;

    ArrayList c4 ;

    Iterator iter1;
    Iterator iter2;
    Iterator iter3;
    Iterator iter4;

    int turn1 = 0;
    int turn2 = 0;
    int turn3 = 0;
    public NewAlgo(DatacenterController dcb)
    {

        dcb.addCloudSimEventListener(this);
        this.vmStatesList = dcb.getVmStatesList();
        this.currentAllocationCounts = Collections.synchronizedMap(new HashMap<Integer, Integer>());
    }
    public void initialize()
    {
        storage = new double[vmStatesList.size()];
        sum = new double[vmStatesList.size()];
        p = new PP[vmStatesList.size()];
        first = new double[vmStatesList.size()];
        second = new double[vmStatesList.size()];
        counter = new int[vmStatesList.size()];
        f = new int[vmStatesList.size()];
        s = new int[vmStatesList.size()];
        for(int i=0;i<vmStatesList.size();i++) {
            storage[i] = 0;
            sum[i] = 0;
            counter[i] = 2;
            first[i] = -1;
            second[i] = -1;
            f[i] = 0;
            s[i] = 0;
        }
        c1 = new ArrayList();
        c2 = new ArrayList();
        c3 = new ArrayList();
        c4 = new ArrayList();
        size = vmStatesList.size();
    }
    public void divideClusters(PP p[])
    {

        double prev = 0;
        int flag = 0;
        double max = -1;
        double diffsum = 0;
        //System.out.println("Size is "+size);
        for(int i=size-1;i>=0;i--)
        {
//            System.out.println("Checking "+p[i].getIndex());
            if(i==size-1)
            {
                prev = p[i].getPower();
                c1.add(p[i].getIndex());
            }
            else if(i==size-2)
            {
                diffsum = p[i].getPower() - prev;
                prev = p[i].getPower();
                max = diffsum;
                flag = 1;
                c1.add(p[i].getIndex());
            }
            else
            {
                int c = 0;
                for(int j=4;j<=10;j++) {
                    //System.out.println(sum[i] - prev + " " +  j* max);

                    if (((p[i].getPower() - prev) >= j * max && max != 0)) {

                        clusterCount = clusterCount + 1;
                        System.out.println("Incremented at"+i);
                        if (i != 0)
                            flag = 0;
                        max = 0;
                        c= 1;
                        break;
                    } else {


                        //System.out.println("Compared with 0 max " + sum[i] + " " + prev * j);
                        if (p[i].getPower() >= prev * j) {
                            System.out.println("beaten and incremented at "+i);
                            clusterCount = clusterCount + 1;
                            if (i != 0)
                                flag = 0;
                            max = 0;
                            c = 1;
                            break;
                        }
                    }
                }
                if (c == 0) {
                    flag = 1;
                    diffsum = p[i].getPower() - prev;
                    if (diffsum > max)
                        max = diffsum;
                }
                if(clusterCount==0)
                    c1.add(p[i].getIndex());
                else if(clusterCount==1)
                    c2.add(p[i].getIndex());
                else if(clusterCount==2)
                    c3.add(p[i].getIndex());
                else
                    c4.add(p[i].getIndex());
                prev = p[i].getPower();
            }

        }

        if(clusterCount==2) {
            turn1 = 9;
        }
        else {
            turn1 = 7;

        }
        if(flag==1)
            clusterCount++;
        if(clusterCount==0)
            clusterCount = 1;

        System.out.println("Cluster count is "+clusterCount);
        System.out.println("contents of cluster 1");
        iter1 = c1.iterator();
        while(iter1.hasNext()){
            System.out.println(iter1.next());
        }
        System.out.println("contents of cluster 2");
        iter2 = c2.iterator();
        while(iter2.hasNext()){
            System.out.println(iter2.next());
        }
        System.out.println("contents of cluster 3");
        iter3 = c3.iterator();
        while(iter3.hasNext()){
            System.out.println(iter3.next());
        }
        System.out.println("contents of cluster 4");
        iter4 = c4.iterator();
        while(iter4.hasNext()){
            System.out.println(iter4.next());
        }
        iter1 = c1.iterator();
        iter2 = c2.iterator();
        iter3 = c3.iterator();
        iter4 = c4.iterator();
    }

    public void sortPowers()
    {
        //System.out.println("came for sorting ");
        for(int i=0;i<vmStatesList.size();i++)
        {
            p[i] = new PP(i,sum[i]);
        }
        Arrays.sort(p);
        /* System.out.println("Sorting Done ");
        for(int i=0;i<vmStatesList.size();i++)
            System.out.println("indexes are "+p[i].getIndex()); */
        for(int i=0;i<vmStatesList.size();i++)
        {
            System.out.print(p[i].getPower()+" ");
            sum[i] = p[i].getPower();
        }
        System.out.println();
//        int r = vmStatesList.size();
//        if(Math.abs((int)(100*p[0].getPower())-(int)(100*p[r-1].getPower()))<=3) {
//            choice = 0;
//          //  System.out.println("choice is made 0");
//        }
//        size = vmStatesList.size();
        divideClusters(p);
    }

    public void check()
    {
        int fail = 0;
        for(int i=0;i<vmStatesList.size();i++)
        {
            if(f[i]==0||s[i]==0)
                fail = 1;
        }
            if(fail==0) {
                flag = 1;
              //  System.out.println("Flag is MAde 1");
            }


    }

    public int getNextAvailableVm(int peakTime) {

/////////////////////////////////////////////////////////////////////////////////////////////////////


        //System.out.println("request arrived ");
        if(begin==0)
        {
            initialize();
            begin = 1;
        }


        if(flag==0)
        {
            vmId++;
            if (vmId >= vmStatesList.size()){
                vmId = 0;
            }
            check();
        }
        else if(init==0) {

           init = 1;
           sortPowers();

           startBalancer  = 1;

        }
        if(startBalancer==1)
        {
            //System.out.println("Came here");
            if(clusterCount==1)
            {
                //System.out.println("Entered cluster 1");
                currVm++;

                if (currVm >= size){
                    currVm = 0;
                }
                vmId = currVm;
                //System.out.println("cluster 1 returned "+vmId);
            }
            else if(clusterCount==2)
            {
                if(peakTime==1)
                {
                    if(iter1.hasNext())
                    {
                        vmId = (int)iter1.next();
                    }
                    else
                    {
                        iter1 = c1.iterator();
                        vmId = (int)iter1.next();
                    }
                }
                else
                {
                    if(iter2.hasNext())
                    {
                        vmId = (int)iter2.next();
                    }
                    else
                    {
                        iter2 = c2.iterator();
                        vmId = (int)iter2.next();
                    }
                }
            }
            else if(clusterCount==3)
            {
                //System.out.println("Entered cluster 3");
                if(peakTime==1)
                {
                    if(turn1>0)
                    {
                        if(iter1.hasNext())
                        {
                            vmId = (int)iter1.next();
                        }
                        else
                        {
                            iter1 = c1.iterator();
                            vmId = (int)iter1.next();
                        }
                        turn1--;
                        if(turn1 == 0)
                        {
                            turn2 = 1;
                        }
                    }
                    else if(turn2>0)
                    {
                        if(iter2.hasNext())
                        {
                            vmId = (int)iter2.next();
                        }
                        else
                        {
                            iter2 = c2.iterator();
                            vmId = (int)iter2.next();
                        }
                        turn2--;
                        if(turn2==0)
                        {
                            turn1 = 9;
                        }
                    }
                }
                else
                {
                    if(iter3.hasNext())
                    {
                        vmId = (int)iter3.next();
                    }
                    else
                    {
                        iter3 = c3.iterator();
                        vmId = (int)iter3.next();
                    }
                }
                //System.out.println("cluster 3 returned "+vmId);
            }
            else
            {
                if(peakTime==1)
                {
                    if(turn1>0)
                    {
                        if(iter1.hasNext())
                        {
                            vmId = (int)iter1.next();
                        }
                        else
                        {
                            iter1 = c1.iterator();
                            vmId = (int)iter1.next();
                        }
                        turn1--;
                        if(turn1==0)
                        {
                           turn2 = 2;

                        }
                    }
                    else if(turn2>0)
                    {
                        if(iter2.hasNext())
                        {
                            vmId = (int)iter2.next();
                        }
                        else
                        {
                            iter2 = c2.iterator();
                            vmId = (int)iter2.next();
                        }
                        turn2--;
                        if(turn2==0)
                        {
                            turn3 = 1;
                        }

                    }
                    else if(turn3>0)
                    {
                        if(iter3.hasNext())
                        {
                            vmId = (int)iter3.next();
                        }
                        else
                        {
                            iter3 = c3.iterator();
                            vmId = (int)iter3.next();
                        }
                        turn3--;
                        if(turn3==0)
                        {
                            turn1 = 7;
                        }
                    }
                }
                else
                {
                    if(iter4.hasNext())
                    {
                        vmId = (int)iter4.next();
                    }
                    else
                    {
                        iter4 = c4.iterator();
                        vmId = (int)iter4.next();
                    }
                }
            }
        }
//        System.out.println("Returning VMid  "+peakTime+" "+vmId);
//        for(int i=0;i<vmStatesList.size();i++)
//            System.out.print(sum[i]+" ");
//        System.out.println();

//        System.out.println("Sending "+vmId);
        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        allocatedVm(vmId);
        return vmId;

    }
    public void cloudSimEventFired(CloudSimEvent e) {
        if (e.getId() == CloudSimEvents.EVENT_CLOUDLET_ALLOCATED_TO_VM){
            int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
            vmStatesList.put(vmId, VirtualMachineState.BUSY);
            storage[vmId] = GridSim.clock();
            if(first[vmId]==-1) {
                first[vmId] = storage[vmId];
               // System.out.println("First for "+vmId+" "+first[vmId]);
            }
            else if(second[vmId]==-1) {
                second[vmId] = storage[vmId];
                //System.out.println("Second for"+vmId+" "+second[vmId]);
            }

        } else if (e.getId() == CloudSimEvents.EVENT_VM_FINISHED_CLOUDLET){
            int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
            vmStatesList.put(vmId, VirtualMachineState.AVAILABLE);

              if(f[vmId]==0)
              {
                  sum[vmId] += GridSim.clock() - first[vmId];
                  f[vmId] = 1;
                  //System.out.println("First cleared for "+vmId+" "+sum[vmId]);
              }
              else if(s[vmId]==0)
              {
                  sum[vmId] += GridSim.clock() - second[vmId];
                  s[vmId] = 1;
                  //System.out.println("Second cleared for"+vmId+" "+sum[vmId]);
              }
        }
    }
}
