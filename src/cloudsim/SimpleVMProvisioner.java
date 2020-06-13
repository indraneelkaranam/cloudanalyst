/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */


package cloudsim;

import gridsim.MachineList;

import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.util.Arrays;
import java.util.HashMap;


/**
 * SimpleVMProvisioner is an VMProvisioner that
 * chooses, as the host for a VM, the host with
 * less PEs in use.
 * public class Capacity implements Comparable<Capacity>
 * //{
 * //
 * //}
 * @author Rodrigo N. Calheiros
 * @since CloudSim Toolkit 1.0 Beta
 * @invariant $none
 */

class Capacity implements Comparable<Capacity>{

	private int index;
	private long power;

	public Capacity(int index,long power)
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

	public long getPower() {
		return power;
	}

	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public int compareTo(Capacity o) {
		long comparePower = ((Capacity)o).getPower();
		return Math.toIntExact(this.power - comparePower);

	}
}


public class SimpleVMProvisioner extends VMProvisioner {
	
	protected HashMap<String, Host> vmTable;
	protected HashMap<String, Integer> usedPEs;
	int[] freePEs;
	long[] freePEpower;
	long overallCapacity = 0;
	long[] rotationCount;
	long mappings = 0;
	int currIndex = 0;
	Capacity[] c;
	Capacity[] low;
	Capacity[] high;
	int lowTurn ;
	int highTurn ;
	int lowPos ;
	int highPos ;
	int lowSize;
	int highSize;
	int size;
	int pointer;

	/**
	 * Creates the new SimpleVMProvisioner object
	 * @pre $none
	 * @post $none
	 */
	public SimpleVMProvisioner() {
		vmTable = new HashMap<String, Host>();
		usedPEs = new HashMap<String, Integer>();
	}
	
	/**
	 * Initializes the values of the fields. This method must be invoked
	 * before starting the actual simulation.
	 * 
	 * @param list Machines available in this Datacentre
	 * @pre $none
	 * @post $none 
	 */
	@Override
	public void init(MachineList list){
		
		super.init(list);
		freePEs = new int[resources.size()];
		freePEpower = new long[resources.size()];
		rotationCount = new long[resources.size()];
		c = new Capacity[resources.size()];

		for (int i=0;i<freePEs.length;i++) {
			freePEs[i] = ((Host) resources.get(i)).getNumPE();
		}
		for(int i=0;i<freePEpower.length;i++) {
			freePEpower[i] = ((Host)resources.get(i)).getMIPSRating();
			overallCapacity+=freePEpower[i];
			c[i] = new Capacity(i,freePEpower[i]);
		}
		Arrays.sort(c);

		//System.out.println("After Sorting ");
		for(Capacity ca:c)
		{
			System.out.println(ca.getIndex()+" "+ca.getPower());
		}

		size = c.length;
		if(size%2==1)
		{
			lowSize = size/2;
			highSize = size/2+1;
			low  = new Capacity[lowSize];
			high = new Capacity[highSize];
			for(int i=0;i<lowSize;i++)
				low[i] = c[i];
			for(int i=lowSize;i<size;i++)
				high[i-lowSize] = c[i];
		}
		else
		{
			lowSize = size/2;
			highSize = size/2;
			low  = new Capacity[lowSize];
			high = new Capacity[highSize];
			for(int i=0;i<lowSize;i++)
				low[i] = c[i];
			for(int i=lowSize;i<size;i++)
				high[i-lowSize] = c[i];
		}
		highTurn = 1;
		lowTurn = 0;
		highPos = highSize-1;
		lowPos = 0;
		pointer = 0;
		//System.out.println("Free PEs power  list");
/*
		///rotation count delivery
		if(freePEpower.length>1) {
			for (int i = 0; i < freePEpower.length; i++) {
				rotationCount[i] = (freePEpower[i] * 100) / overallCapacity;

				//rotationCount[i] = overallCapacity / freePEpower[i];
				//rotationCount[i] = 100-rotationCount[i];
				//System.out.println("Rotation count " + rotationCount[i]);
			}
		}
		else
		{
			rotationCount[0] = overallCapacity / freePEpower[0];
		}
		mappings = rotationCount[0];
		currIndex = 0;*/
	}
	
	/**
	 * Allocates a host for a given VM.
	 * @param vm VM specification
	 * @return $true if the host could be allocated; $false otherwise
	 * @pre $none
	 * @post $none
	 */
	@Override
	public boolean allocateHostForVM(VMCharacteristics vm) {
	
		int requiredPEs = vm.getCpus();
		boolean result=false;
		int tries=0;
		int[] freePEsTemp = freePEs.clone();
		
		if(!this.vmTable.containsKey(vm.getVmId()+"-"+vm.getUserId())){//if this vm was not created
		
			do{//we still trying until we find a host or untill we try all of them
				int moreFree=Integer.MIN_VALUE;
				int idx=-1;
////////////////////////////////////////////////////////////////////////////////////////////

/*				idx = c[pointer].getIndex();
				pointer++;
				if(pointer>=size)
					pointer = 0; */

/////////////////////////////////////////////////////////////////////////////////////////////

			/*	if(highTurn==1)
				{
					idx = high[highPos].getIndex();
					highPos--;
					if(highPos<0)
						highPos = highSize-1;
					if(lowSize>0) {
						highTurn = 0;
						lowTurn = 1;
					}
					System.out.print("Assigned Higher one "+idx);
				}
				else
				{
					idx = low[lowPos].getIndex();
					lowPos++;
					if(lowPos>=lowSize)
						lowPos = 0;
					lowTurn--;
					if(lowTurn==0) {
						highTurn = 1;
						lowTurn = 0;
					}
					System.out.print("Assigned Lower one "+idx);
				}*/


//////////////////////////////////////////////////////////////////////////////////////////////


/*				if(mappings>0) {
					idx = currIndex;
					mappings--;
				}
				else
				{
					currIndex = (currIndex+1)%freePEsTemp.length;
					mappings = rotationCount[currIndex];
					idx = currIndex;
					mappings--;
				}*/

///////////////////////////////////////////////////////////////////////////////////////////

				//we want the host with less pes in use
			   for(int i=0;i<freePEsTemp.length;i++){
					if(freePEsTemp[i]>moreFree){

						moreFree=freePEsTemp[i];
						idx=i;
					}
				}

				System.out.println("Returning idx  "+idx);
				Host host = (Host)resources.get(idx);
				result = host.vmCreate(vm);
			    // System.out.println("Assigning vmid "+vm.getVmId()+"to idx "+idx);
				if(result){//if vm were succesfully created in the host
					vmTable.put(vm.getVmId()+"-"+vm.getUserId(),host);
					usedPEs.put(vm.getVmId()+"-"+vm.getUserId(),requiredPEs);
					freePEs[idx]-=requiredPEs;
					result=true;
					break;
				} else {
					freePEsTemp[idx]=Integer.MIN_VALUE;
				}
			
				tries++;
			}while(!result && tries<freePEs.length);
			
		}//if
		
		return result;
	}
	
	/**
	 * Releases the host used by a VM
	 * @param vmID ID form the vm that is releasing the host
	 * @param userID ID of VM's owner
	 * @pre $none
	 * @post none
	 */
	@Override
	public void deallocateHostForVM(int vmID, int userID) {		
		Host host = vmTable.remove(vmID+"-"+userID);
		int idx = resources.indexOf(host);
		Integer pes = usedPEs.remove(vmID+"-"+userID);
		if(host!=null) {
			host.vmDestroy(vmID,userID);
			freePEs[idx]+=pes;
		}
	}
	
	/**
	 * Triggers a migration from a given virtual machine to a selected
	 * host
	 * @param vmID ID from the virtual machine that will migrate
	 * @param newHostID ID from the host that will receive the VM
	 * @return $true if the migration succeeds; $false otherwise
	 * @pre $none
	 * @post $none
	 */
	@Override
	public boolean migrateVM(int vmID, int userID, int newHostID) {
		
		//where is this VM running?
		Host source = getHost(vmID,userID);
		VirtualMachine vm = source.vmMigrate(vmID, userID);
		if(vm==null) return false;
		
		Host destination = (Host) resources.getMachine(newHostID);
		if(destination==null) return false;
		
		if(destination.getNumFreePE()>=vm.getCpus()){
			return destination.vmMigrate(vm);
		}
		
		return false;
	}
	
	/**
	 * Gets the host that is executing the given VM belonging to the
	 * given user 
	 * 
	 * @param vmID ID from the virtual machine that will migrate
	 * @param userID ID from the owner of the VM
	 * @return the Host with the given vmID and userID; $null if not found
	 * @pre $none
	 * @post $none
	 */
	@Override
	public Host getHost(int vmID, int userID) {
		return vmTable.get(vmID+"-"+userID);
	}
}
