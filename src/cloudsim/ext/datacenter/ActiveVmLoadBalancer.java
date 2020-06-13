package cloudsim.ext.datacenter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import cloudsim.ext.Constants;
import cloudsim.ext.event.CloudSimEvent;
import cloudsim.ext.event.CloudSimEventListener;
import cloudsim.ext.event.CloudSimEvents;
import gridsim.GridSim;

/**
 * ActiveVmLoadBalancer load balances the tasks among available VM's in a way to even out
 * the number of active tasks at any given time on each VM.
 * 
 * @author Bhathiya Wickremasinghe
 *
 */
public class ActiveVmLoadBalancer extends VmLoadBalancer implements CloudSimEventListener {
	/** Holds the count current active allcoations on each VM */
	private Map<Integer, Integer> currentAllocationCounts;
	
	private Map<Integer, VirtualMachineState> vmStatesList;

	private Map<Integer, Integer> pendingRequests;

	private Map<Integer, Integer> completedRequests;

	double storage[] = new double[1000];;
	double sum[] = new double[1000]; ;

	public ActiveVmLoadBalancer(DatacenterController dcb){
		dcb.addCloudSimEventListener(this);
		this.vmStatesList = dcb.getVmStatesList();
		this.currentAllocationCounts = Collections.synchronizedMap(new HashMap<Integer, Integer>());
		pendingRequests = new HashMap<Integer, Integer>();
		completedRequests =  new HashMap<Integer, Integer>();
	}

	/**
	 * @return The VM id of a VM so that the number of active tasks on each VM is kept
	 * 			evenly distributed among the VMs.
	 */
	@Override
	public int getNextAvailableVm(int peakTime){
		int vmId = -1;
////////////////////////////////////////////////////////////////////////////////////////////////\

		if (currentAllocationCounts.size() < vmStatesList.size()){
			for (int availableVmId : vmStatesList.keySet()){
				if (!currentAllocationCounts.containsKey(availableVmId)){
					vmId = availableVmId;
					break;
				}
			}
		} else {
			int currCount;
			int minCount = Integer.MAX_VALUE;

			for (int thisVmId : currentAllocationCounts.keySet()){
				currCount = currentAllocationCounts.get(thisVmId);
				if (currCount < minCount){
					minCount = currCount;
					vmId = thisVmId;
				}
			}
		}

////////////////////////////////////////////////////////////////////////////////////////////////
		//Find the vm with least number of allocations
		
		//If all available vms are not allocated, allocated the new ones
		/*if (pendingRequests.size() < vmStatesList.size()){
			for (int availableVmId : vmStatesList.keySet()){
				if (!pendingRequests.containsKey(availableVmId)){
					vmId = availableVmId;
					pendingRequests.put(vmId,1);

					break;
				}				
			}
		} else {
			int currCount;
			int minCount = Integer.MAX_VALUE;


			for (int thisVmId : pendingRequests.keySet()){
				Integer temp1 = currentAllocationCounts.get(thisVmId);
				if(temp1==null)
					temp1 = 0;
				Integer temp2 = pendingRequests.get(thisVmId);
				if(temp2==null)
					temp2 = 0;
				Integer temp3 = completedRequests.get(thisVmId);
				if(temp3==null)
					temp3 = 0;
				currCount = temp1+temp2+temp3;
				if (currCount < minCount){
					minCount = currCount;
					vmId = thisVmId;
				}
			}

			pendingRequests.put(vmId,pendingRequests.get(vmId)+1);
		}*/
//////////////////////////////////////////////////////////////////////////////////////////////////////
		allocatedVm(vmId);
		
		return vmId;
		
	}
	
	public void cloudSimEventFired(CloudSimEvent e) {

		if (e.getId() == CloudSimEvents.EVENT_CLOUDLET_ALLOCATED_TO_VM){
			int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);

			Integer currCount = currentAllocationCounts.get(vmId);
			if (currCount == null){
				currCount = 1;
			} else {
				currCount++;
			}
			currentAllocationCounts.put(vmId, currCount);

//			int pendCount = pendingRequests.get(vmId);
//			pendCount--;
//			pendingRequests.put(vmId,pendCount);
//			storage[vmId] = GridSim.clock();


		} else if (e.getId() == CloudSimEvents.EVENT_VM_FINISHED_CLOUDLET){
			int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
			Integer currCount = currentAllocationCounts.remove(vmId);
			if (currCount != null){
				currCount--;
				currentAllocationCounts.put(vmId, currCount);
			}
//			currCount = completedRequests.get(vmId);
			if(currCount==null)
				currCount = 0;
			else
				currCount++;
//			completedRequests.put(vmId,currCount);
//			sum[vmId] += GridSim.clock() - storage[vmId];
		}

	}
	


}
