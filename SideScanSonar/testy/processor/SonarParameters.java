package processor;

public class SonarParameters {
	double speedOfSound;
	double samplingFrequency;
	int depthRiseCount = 6;
	int depthMinSample = (int)(38000.0/1460.d*1.0d/0.2d);
	
	public SonarParameters(double v, double rate){
		speedOfSound = v;
		samplingFrequency = rate;
	}
	
	public double getV(){
		return speedOfSound;
	}
	
	public double getRate(){
		return samplingFrequency;
	}
}
