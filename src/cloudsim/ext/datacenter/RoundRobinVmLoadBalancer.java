package cloudsim.ext.datacenter;

import java.util.Map;

/**
 * This class implements {@link VmLoadBalancer} with a Round Robin policy.
 * 
 * @author Bhathiya Wickremasinghe
 *
 */
public class RoundRobinVmLoadBalancer extends VmLoadBalancer {
	
	private Map<Integer, VirtualMachineState> vmStatesList;
	private int currVm = -1;
	private int peakcurrvm = 0;
	private int normalcurrvm = 1;
	public RoundRobinVmLoadBalancer(Map<Integer, VirtualMachineState> vmStatesList){
		super();
		
		this.vmStatesList = vmStatesList;
	}

	/* (non-Javadoc)
	 * @see cloudsim.ext.VMLoadBalancer#getVM()
	 */
	public int getNextAvailableVm(int peakTime){


		/*if(peakTime==1)
		{
			allocatedVm(peakcurrvm);
			int x = peakcurrvm;
			peakcurrvm+=2;
			if(peakcurrvm>=vmStatesList.size())
				peakcurrvm = 0;
			return x;
		}
		else
		{
			allocatedVm(normalcurrvm);
			int x = normalcurrvm;
			normalcurrvm+=2;
			if(normalcurrvm>=vmStatesList.size())
				normalcurrvm=1;
			return x;
		} */
		currVm++;
		
		if (currVm >= vmStatesList.size()){
			currVm = 0;
		}
		
		allocatedVm(currVm);
		
		return currVm;
		
	}
}
