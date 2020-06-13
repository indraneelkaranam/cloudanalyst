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
    PP p[] ;
    int hEnd;
    int lEnd;
    int hLimit;
    int lLimit;
    int begin = 0;
    int choice = 1;
    int currVm = -1;
    int vmId = -1;
    int size;
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
        counter = new int[vmStatesList.size()];
        for(int i=0;i<vmStatesList.size();i++) {
            storage[i] = 0;
            sum[i] = 0;
            counter[i] = 2;
        }
        size = vmStatesList.size();
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
        }
        System.out.println();
        int r = vmStatesList.size();
        if(Math.abs((int)(100*p[0].getPower())-(int)(100*p[r-1].getPower()))<=3) {
            choice = 0;
          //  System.out.println("choice is made 0");
        }

        int n =  vmStatesList.size();
        hEnd = vmStatesList.size()-1;
        lLimit = 0;
        if(n%2==0)
        {
            lEnd = n/2-1;
        }
        else if(n%2==1)
        {
            lEnd = (n+1)/2 - 1;
        }
//        if(n<10)
//            lEnd = 1;
//        else if(n==10)
//            lEnd = 2;
//        else if(n>=20)
//            lEnd = 5;
        hLimit = lEnd+1;
    }

    public void check()
    {
        int l = 0;
        for(int i=0;i<vmStatesList.size();i++)
            if(counter[i]>0)
                l = 1;
            if(l==0) {
                flag = 1;
              //  System.out.println("Flag is MAde 1");
            }


    }

    public int getNextAvailableVm(int peakTime) {

/////////////////////////////////////////////////////////////////////////////////////////////////////

        //System.out.println("Request Received ");

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



            if(choice==1) {
                if (peakTime == 1) {
                    vmId = p[hEnd].getIndex();

                    hEnd--;
                    if (hEnd < hLimit) {
                        hEnd = vmStatesList.size() - 1;
//                        hLimit++;
//                        if (hLimit > hEnd)
//                            hLimit = vmStatesList.size() / 2;
                    }
                } else {
                    vmId = p[lEnd].getIndex();
                    lEnd--;
                    if (lEnd < lLimit) {
                        lEnd = vmStatesList.size() / 2 - 1;
//                        lLimit++;
//                        if (lLimit > lEnd)
//                            lLimit = 0;
                    }
                }
            }
            else
            {
                currVm++;

                if (currVm >= vmStatesList.size()){
                    currVm = 0;
                }
                vmId = currVm;
//                if (vmStatesList.size() > 0){
//                    int temp;
//                    for (Iterator<Integer> itr = vmStatesList.keySet().iterator(); itr.hasNext();){
//                        temp = itr.next();
//                        VirtualMachineState state = vmStatesList.get(temp); //System.out.println(temp + " state is " + state + " total vms " + vmStatesList.size());
//                        if (state.equals(VirtualMachineState.AVAILABLE)){
//                            vmId = temp;
//                            break;
//                        }
//                    }
//                }
            }

        }
        else
        {

//                   vmId = -1;
//                  if(peakTime==1)
//                  {
//                        for(int i=size-1;i>=0;i--)
//                        {
//                            VirtualMachineState state = vmStatesList.get(p[i].getIndex());
//                            if (state.equals(VirtualMachineState.AVAILABLE))
//                            {
//                                vmId = p[i].getIndex();
//                                break;
//                            }
//                        }
//                      if(vmId==-1)
//                      {
//                          vmId = p[size-1].getIndex();
//                      }
//                  }
//                  else
//                  {
//                      for(int i=0;i<size;i++)
//                      {
//                          VirtualMachineState state = vmStatesList.get(p[i].getIndex());
//                          if (state.equals(VirtualMachineState.AVAILABLE))
//                          {
//                              vmId = p[i].getIndex();
//                              break;
//                          }
//                      }
//                      if(vmId==-1)
//                      {
//                          vmId = p[size-1].getIndex();
//                      }
//                  }
//                int currCount;
//                int minCount = Integer.MAX_VALUE;
//
//                for (int i=0;i<vmStatesList.size();i++){
//                    int thisVmId = p[i].getIndex();
//                    currCount = currentAllocationCounts.get(thisVmId);
//                    if (currCount < minCount){
//                        minCount = currCount;
//                        vmId = thisVmId;
//                    }
//                }

              if(choice==1) {
                if (peakTime == 1) {
                    vmId = p[hEnd].getIndex();
                    hEnd--;
                    if (hEnd < hLimit) {
                        hEnd = vmStatesList.size() - 1;
//                        hLimit++;
//                        if (hLimit > hEnd)
//                            hLimit = vmStatesList.size() / 2;
                       }
                } else {
                    vmId = p[lEnd].getIndex();
                    lEnd--;
                    if (lEnd < lLimit) {
                        lEnd = vmStatesList.size() / 2 - 1;
//                        lLimit++;
//                        if (lLimit > lEnd)
//                            lLimit = 0;
                    }
                }
            }
            else
            {
                currVm++;

                if (currVm >= vmStatesList.size()){
                    currVm = 0;
                }
                vmId = currVm;
//                if (vmStatesList.size() > 0){
//                    int temp;
//                    for (Iterator<Integer> itr = vmStatesList.keySet().iterator(); itr.hasNext();){
//                        temp = itr.next();
//                        VirtualMachineState state = vmStatesList.get(temp); //System.out.println(temp + " state is " + state + " total vms " + vmStatesList.size());
//                        if (state.equals(VirtualMachineState.AVAILABLE)){
//                            vmId = temp;
//                            break;
//                        }
//                    }
//                }
            }
        }
//        System.out.println("Returning VMid  "+peakTime+" "+vmId);
//        for(int i=0;i<vmStatesList.size();i++)
//            System.out.print(sum[i]+" ");
//        System.out.println();


        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        allocatedVm(vmId);
        return vmId;

    }
    public void cloudSimEventFired(CloudSimEvent e) {
        if (e.getId() == CloudSimEvents.EVENT_CLOUDLET_ALLOCATED_TO_VM){
            int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
            vmStatesList.put(vmId, VirtualMachineState.BUSY);
            storage[vmId] = GridSim.clock();
//            Integer currCount = currentAllocationCounts.get(vmId);
//            if (currCount == null){
//                currCount = 1;
//            } else {
//                currCount++;
//            }
//            currentAllocationCounts.put(vmId, currCount);

        } else if (e.getId() == CloudSimEvents.EVENT_VM_FINISHED_CLOUDLET){
            int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
            vmStatesList.put(vmId, VirtualMachineState.AVAILABLE);

//            Integer currCount = currentAllocationCounts.remove(vmId);
//            if (currCount != null){
//                currCount--;
//                currentAllocationCounts.put(vmId, currCount);
//            }
//
//            if(currCount==null)
//                currCount = 0;
//            else
//                currCount++;
           if(counter[vmId]>0) {
               sum[vmId] += GridSim.clock() - storage[vmId];
//                System.out.println("completed execution " + vmId + " " + sum[vmId]);
                counter[vmId]--;
            }
        }
    }
}
