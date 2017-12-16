
import java.io.*;
import java.util.*;
import org.apache.commons.math3.distribution.LaplaceDistribution;

public class CMHeavyHitters{
	public List<Integer> heavyhitters(List<Integer> userData,int start,int end,double epsi,double sigma,double threshold){
		int w=(int)Math.ceil(Math.E/epsi);
		int d=(int)Math.ceil(Math.log(1/sigma));
		int[][] CM=new int[d][w];
		List<hashparameters> hashfucs=new ArrayList<hashparameters>();
		Map<Integer,Integer> candidateData=new HashMap<Integer,Integer>();
		List<Integer> res=new ArrayList<Integer>();
		for(int i=0;i<d;i++){
			hashfucs.add(new hashparameters(1,(int)(Math.random()*w),w));
		}
		int cnt=0;
		for(int i=0;i<userData.size();i++){
			cnt++;
			for(int j=0;j<d;j++){
				hash(hashfucs,j,userData.get(i),CM);	
			}
			int qi=minCount(hashfucs,userData.get(i),CM);
			if(qi>threshold*cnt){
				candidateData.put(userData.get(i),qi);
			}

			checkDelete(candidateData,(int)threshold*cnt);
		}
		res=checkDeleteAll(candidateData,(int)threshold*cnt);
		return res;
	}
	/*
	public List<Integer> heavyhitterswithLaplaceNoise(List<Integer> userData,int start,int end,double epsi,double sigma,double threshold){
		int w=(int)Math.ceil(Math.E/epsi);
		int d=(int)Math.ceil(Math.log(1/sigma));
		int[][] CM=new int[d][w];
		List<hashparameters> hashfucs=new ArrayList<hashparameters>();
		Map<Integer,Double> candidateData=new HashMap<Integer,Double>();
		List<Integer> res=new ArrayList<Integer>();
		for(int i=0;i<d;i++){
			hashfucs.add(new hashparameters(1,(int)(Math.random()*w),w));
		}
		int cnt=0;
		for(int i=0;i<userData.size();i++){
			cnt++;
			for(int j=0;j<d;j++){
				hash(hashfucs,j,userData.get(i),CM);	
			}
			double qi=minCount(hashfucs,userData.get(i),CM)+LaplaceNoise(0,0.4);//add laplace noise here
			if(qi>threshold*cnt){
				candidateData.put(userData.get(i),qi);
			}

			checkDeleteLaplace(candidateData,(int)threshold*cnt);
		}
		res=checkDeleteAllLaplace(candidateData,(int)threshold*cnt);
		return res;
	}
	*/
    
	private double LaplaceNoise(double mean,double epsilon){
		LaplaceDistribution i=new LaplaceDistribution(mean,1/epsilon);
		return i.sample();
	
	}
	private int minCount(List<hashparameters> hashfucs,int data,int[][] CM){
		int min=Integer.MAX_VALUE;
		for(int i=0;i<CM.length;i++){
			if(CM[i][(hashfucs.get(i).a*data+hashfucs.get(i).b)%hashfucs.get(i).r]<min){
				min=CM[i][(hashfucs.get(i).a*data+hashfucs.get(i).b)%hashfucs.get(i).r];
			}
		}
		return min;
	}

	public List<Integer> generateUserdata(int start,int end,int num,String method){
		List<Integer> data=new ArrayList<Integer>();
		Random r=new Random();
		for(int i=0;i<num;i++){
			int d=(int)Math.round(r.nextGaussian()*50+(start+end)/2);
			if(d>=start&&d<=end) 
				data.add(d);
			else if(d<start) data.add(start);
			else if(d>end) data.add(end);
		}
		return data;
	}
	/*
	private void checkDeleteLaplace(Map<Integer,Double> candidateData,int thres){
		Iterator it=candidateData.entrySet().iterator();
		int minKey=-1;
		double minVal=(double)Integer.MAX_VALUE;
		while(it.hasNext()){
			Map.Entry me=(Map.Entry) it.next();
			double val=(Double)me.getValue();
			if(val<minVal){
				minVal=(Double)me.getValue();
				minKey=(Integer)me.getKey();
			}
		}
		if(candidateData.containsKey(minKey)&&candidateData.get(minKey)<=thres){
			candidateData.remove(minKey);
			System.out.println("out");
		}

	}
	*/
	private void checkDelete(Map<Integer,Integer> candidateData,int thres){
		Iterator it=candidateData.entrySet().iterator();
		int minKey=-1,minVal=Integer.MAX_VALUE;
		while(it.hasNext()){
			Map.Entry me=(Map.Entry) it.next();
			int val=(Integer)me.getValue();
			if(val<minVal){
				minVal=(Integer)me.getValue();
				minKey=(Integer)me.getKey();
			}
		}
		if(candidateData.containsKey(minKey)&&candidateData.get(minKey)<=thres){
			candidateData.remove(minKey);
		}

	}
	/*
	private List<Integer> checkDeleteAllLaplace(Map<Integer,Double> candidateData,int thres){
		List<Integer> res=new ArrayList<Integer>();
		Iterator it=candidateData.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me=(Map.Entry) it.next();
			res.add((Integer)me.getKey());
		}
		for(int i=0;i<res.size();i++){
			if(candidateData.get(res.get(i))<=thres){
				res.remove(i);
				i--;
			}
		}
		return res;
	}
	*/
	private List<Integer> checkDeleteAll(Map<Integer,Integer> candidateData,int thres){
		List<Integer> res=new ArrayList<Integer>();
		Iterator it=candidateData.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me=(Map.Entry) it.next();
			res.add((Integer)me.getKey());
		}
		for(int i=0;i<res.size();i++){
			if(candidateData.get(res.get(i))<=thres){
				res.remove(i);
				i--;
			}
		}
		return res;
	}
	
	private void hash(List<hashparameters> hashfucs,int j,int d,int[][] CM){
		CM[j][(hashfucs.get(j).a*d+hashfucs.get(j).b)%hashfucs.get(j).r]++;
		return;
	}
	public List<Integer> addNoisetoData(List<Integer> data,double mean,double epsilon,int start,int end){
		List<Integer> res=new ArrayList<Integer>();
		for(int i=0;i<data.size();i++){
			int can=data.get(i)+(int)LaplaceNoise(mean,epsilon);
			if(can>=start&&can<=end)
				res.add(can);
			else{
				if(can<start)
					res.add(start);
				else if(can>end)
					res.add(end);
			}
		}
		return res;
	}
	public static void main(String[] args){
		CMHeavyHitters h=new CMHeavyHitters();
		List<Integer> data=new ArrayList<Integer>();
		List<Integer> NoiseData=new ArrayList<Integer>();
		List<Integer> hhitters=new ArrayList<Integer>();
		data=h.generateUserdata(0,1000,500,"Gaussian");
		hhitters=h.heavyhitters(data,0,1000,0.01,0.1,0.05);
		System.out.println(hhitters);
		NoiseData=h.addNoisetoData(data,0,0.2,0,1000);
		hhitters=h.heavyhitters(NoiseData,0,1000,0.01,0.1,0.05);
		System.out.println(hhitters);
		//hhitters=h.heavyhitters(data,0,1000,0.01,0.1,0.05);
		//System.out.println(hhitters);
	}
}

class hashparameters{
	int a;
	int b;
	int r;
	hashparameters(int ap,int bp,int rp){
		a=ap;
		b=bp;
		r=rp;
	}

}



