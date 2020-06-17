package cloudsim.ext.servicebroker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cloudsim.DataCenter;
import cloudsim.ext.Simulation;
import cloudsim.ext.GeoLocatable;
import cloudsim.ext.InternetCharacteristics;
import cloudsim.ext.datacenter.DatacenterController;
import cloudsim.DatacenterCharacteristics;
import gridsim.MachineList;

import static cloudsim.ext.Simulation.getDcs1;


public class ServiceProximityServiceBroker implements CloudAppServiceBroker {

	static int use = 0;


	int ongo = 0 ;
	long restore = 0;
	long leftMappings = 0;
	protected Map<Integer, List<String>> regionalDataCenterIndex = null;
	protected Map<String, Long> regionSpeed = null;
	protected Map<String, Long> percentageSpeed = null;
	protected Map<String, Integer> marker = null;
	protected Map<String, Integer> vmCount = null;
    private Map<String, DatacenterController> dataCenters;
    protected  Map<Integer, String> permanentDc= null;
	public ServiceProximityServiceBroker(){
		regionalDataCenterIndex = new HashMap<Integer, List<String>>();
		
		init();
	}
	
	protected void init(){
		List<GeoLocatable> allInternetEntities = InternetCharacteristics.getInstance().getAllEntities();
		int region;
		regionSpeed = new HashMap<String, Long>();
		percentageSpeed = new HashMap<String, Long>();
		marker = new HashMap<String, Integer>();
        vmCount = new HashMap<String, Integer>();
        permanentDc = new HashMap<Integer, String>();
		for (GeoLocatable entity : allInternetEntities){
			if (entity instanceof DatacenterController){
				region = entity.getRegion();
				List<String> l = regionalDataCenterIndex.get(region);
				if (l == null){
					l = new ArrayList<String>();
					regionalDataCenterIndex.put(region, l);
				}
				l.add(entity.get_name()); //which regions has which DC
			}
		}
		List<DataCenter>  Dcs1 =  Simulation.getDcs1();
		List<DatacenterController> Dcb1 = Simulation.getDcbs1();

		for(DataCenter temp : Dcs1)
		{
			String name = temp.getDc_name();
			DatacenterCharacteristics resource_ = temp.getResource_();
			MachineList machineList = resource_.getMachineList();
			//int x = machineList.getNumPE();
			regionSpeed.put(name+"-Broker",resource_.getOverallspeed());
			//System.out.println("No of PEs "+x+resource_.getOverallspeed());
		}
        dataCenters = new HashMap<String, DatacenterController>();
        for (DatacenterController dcb : Dcb1){
            dataCenters.put(dcb.get_name(), dcb);

        }
	}
	
	public String getDestination(GeoLocatable inquirer) {  //invoked for each cloudlet


		List<Integer> proximityList = InternetCharacteristics.getInstance().getProximityList(inquirer.getRegion());
		//proximityList.size() = 6(all regions)
		int region;
		String dcName;
		for (int i = 0; i < proximityList.size(); i++){
			region = proximityList.get(i);  //most cases its self region for i=0;
			dcName = getAnyDataCenter(region);
			if (dcName != null){
				return dcName;
			}
		}
		
		//If it comes here, that means there are no DC's anywhere
		throw new RuntimeException("Looks like you have not configured any Data Centers. Please check the configuration");
	}

	protected String getAnyDataCenter(int region) {
		List<String> regionalList = regionalDataCenterIndex.get(region); //all data centers in that region
		String dcName = null;


		if (regionalList != null){
			int listSize = regionalList.size();
			if (listSize == 1){
				dcName = regionalList.get(0);
			} else {
                //////////////////////////////default algo//////////////////////////////////////
//				More than one candidate
//				 Load balance between them
				int rand = (int) (Math.random() * listSize);
				dcName = regionalList.get(rand);
                //////////////////////////////default algo//////////////////////////////////////

               /////////////////percentage sharing Algo///////////////////////////////////////////////
				/*if(!marker.containsKey(regionalList.get(0))) {

					long totalspeed = 0;
					for(int i=0;i<listSize;i++) {
						totalspeed += regionSpeed.get(regionalList.get(i));

						System.out.println("total speed for "+regionalList.get(0)+" "+totalspeed);
						marker.put(regionalList.get(i),1);
					}
					for(int i=0;i<listSize;i++)
					{
						Long frac = (regionSpeed.get(regionalList.get(i))*100)/totalspeed;
						percentageSpeed.put(regionalList.get(i),(Long)(frac));
						long one  = 1;
						if((Long)(frac)==0)
							percentageSpeed.put(regionalList.get(i),one);
						System.out.println("regionalList "+regionalList.get(i)+" gets "+(Long)(frac));
					}

					ongo = 0;
					restore = percentageSpeed.get(regionalList.get(ongo));
					leftMappings = restore;
				}

				long  max = 0;

				if(leftMappings>0)
				{
					leftMappings--;

					dcName =  regionalList.get(ongo);
				}
				else
				{
					percentageSpeed.put(regionalList.get(ongo),restore);
					ongo = (ongo+1)%listSize;
					restore = percentageSpeed.get(regionalList.get(ongo));
					leftMappings = restore;
					leftMappings--;

					dcName = regionalList.get(ongo);

				}*/
                /////////////////////////////percentage sharing Algo///////////////////////////////////////////////

                 /////////////////////////////////firstAttemptAlgo////////////////////////////////////////

//				for(int i=0;i<listSize;i++)
//				{
//				//	System.out.println("region obtained "+ regionalList.get(i));
//
//					if(regionSpeed.get(regionalList.get(i))>max)
//					{
//						max = regionSpeed.get(regionalList.get(i));
//						dcName = regionalList.get(i);
//					}
//				}
                /////////////////////////////////firstAttemptAlgo////////////////////////////////////////

                /////////////////VMexperiment algo///////////////////////////////////////////////////////////

 /*               if(!marker.containsKey(regionalList.get(0))) {

                    int max = 0;
                    for (int i = 0; i < listSize; i++) {

                        String Dcname = regionalList.get(i);
                        marker.put(Dcname,1);
                        DatacenterController dcb = dataCenters.get(Dcname);
                        //System.out.println("Getting Vms for " + Dcname + " " + dcb.getVmStatesList().size());
                        vmCount.put(Dcname, dcb.getVmStatesList().size());

                        if(dcb.getVmStatesList().size()>max)
                        {
                            max = dcb.getVmStatesList().size();
                            dcName = Dcname;
                        }
                    }
                    permanentDc.put(region,dcName);
                }
                return permanentDc.get(region); */
                /////////////////VMexperiment algo///////////////////////////////////////////////////////////
			}
		}

		return dcName;
	}

}
