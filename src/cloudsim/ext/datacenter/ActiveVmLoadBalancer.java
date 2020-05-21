package cloudsim.ext.datacenter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import cloudsim.ext.Constants;
import cloudsim.ext.event.CloudSimEvent;
import cloudsim.ext.event.CloudSimEventListener;
import cloudsim.ext.event.CloudSimEvents;

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

	//private  Map<Integer, Integer> usagePurpose;

	//private  Map<Integer, Integer> pqPurpose;
	PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>(
			(a,b) -> a.getValue()==b.getValue() ? b.getKey().compareTo(a.getKey()) : a.getValue()-b.getValue()
	);

	public ActiveVmLoadBalancer(DatacenterController dcb){
		dcb.addCloudSimEventListener(this);
		this.vmStatesList = dcb.getVmStatesList();
		this.currentAllocationCounts = Collections.synchronizedMap(new HashMap<Integer, Integer>());
		pendingRequests = new HashMap<Integer, Integer>();
		//usagePurpose =  new HashMap<Integer, Integer>();
		//pqPurpose = new HashMap<Integer, Integer>();
		for (int availableVmId : vmStatesList.keySet()){
			pendingRequests.put(availableVmId,0);
			//pqPurpose.put(availableVmId,0);
		}
		/*for(Map.Entry<Integer, Integer> entry: pendingRequests.entrySet())
		{
			pq.offer(entry);
		}*/
	}

	/**
	 * @return The VM id of a VM so that the number of active tasks on each VM is kept
	 * 			evenly distributed among the VMs.
	 */
	@Override
	public int getNextAvailableVm(){
		int vmId = -1;

		/*Map.Entry<Integer, Integer> temp;         //testing purpose
		temp = pq.poll();

		vmId = temp.getKey();

		int count = temp.getValue();

		Integer alloCount = currentAllocationCounts.get(vmId);

		if(alloCount==null)
			alloCount = 0;
		pendingRequests.put(vmId,count+1);


		usagePurpose.clear();
		usagePurpose.put(vmId,alloCount+count+1);
		pqPurpose.put(vmId,alloCount+count+1);

		for(Map.Entry<Integer, Integer> entry: usagePurpose.entrySet())
			pq.offer(entry);

		usagePurpose.clear();  */                  //testing purpose





		//Find the vm with least number of allocations
		
		//If all available vms are not allocated, allocated the new ones
		if (currentAllocationCounts.size() < vmStatesList.size()){
			for (int availableVmId : vmStatesList.keySet()){
				if (!currentAllocationCounts.containsKey(availableVmId)){
					vmId = availableVmId;
					pendingRequests.put(vmId,1);

					break;
				}				
			}
		} else {
			int currCount;
			int minCount = Integer.MAX_VALUE;


			for (int thisVmId : pendingRequests.keySet()){
				Integer temp = currentAllocationCounts.get(thisVmId);
				if(temp==null)
					temp = 0;
				currCount = pendingRequests.get(thisVmId)+temp;
				if (currCount < minCount){
					minCount = currCount;
					vmId = thisVmId;
				}
			}
			pendingRequests.put(vmId,minCount+1);
		}
		
		allocatedVm(vmId);
		
		return vmId;
		
	}
	
	public void cloudSimEventFired(CloudSimEvent e) {

		if (e.getId() == CloudSimEvents.EVENT_CLOUDLET_ALLOCATED_TO_VM){
			int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);

			Integer currCount = currentAllocationCounts.remove(vmId);
			if (currCount == null){
				currCount = 1;
			} else {
				currCount++;
			}
			
			currentAllocationCounts.put(vmId, currCount);

			Integer pendCount = pendingRequests.remove(vmId);
			pendCount--;
			pendingRequests.put(vmId,pendCount);


			/*usagePurpose.clear();
			usagePurpose.put(vmId,pqPurpose.get(vmId));
			pq.remove(usagePurpose);
			usagePurpose.clear();
			usagePurpose.put(vmId,pendCount+currCount+1);
			for(Map.Entry<Integer, Integer> entry: usagePurpose.entrySet())
				pq.offer(entry);

			usagePurpose.clear();*/


		} else if (e.getId() == CloudSimEvents.EVENT_VM_FINISHED_CLOUDLET){
			int vmId = (Integer) e.getParameter(Constants.PARAM_VM_ID);
			Integer currCount = currentAllocationCounts.remove(vmId);
			if (currCount != null){
				currCount--;
				currentAllocationCounts.put(vmId, currCount);
			}
		}

	}
	


}
