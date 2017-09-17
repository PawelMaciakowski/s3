package com.pm.s3.processing.filters;

import com.pm.s3.data.types.LongData;
import com.pm.s3.data.types.PositionData;

public class PositionFilter {
	
	public static void filter(PositionData[] posDatas, LongData timeDatas[], double freq) {
		double RC = 1.0/(2.0*Math.PI*freq);
		double dt;
		
		double[][] outXf=new double[posDatas.length][],outYf=new double[posDatas.length][], outXb=new double[posDatas.length][], outYb=new double[posDatas.length][];
		
		// FORWARD
		for (int i=0; i<posDatas.length; i++) {
			int pts = posDatas[i].getLength();
			outXf[i] = new double[pts];
			outYf[i] = new double[pts];
			
			if (i==0) {
				outXf[0][0]=posDatas[0].getEasting(0);
				outYf[0][0]=posDatas[0].getNorthing(0);
			}else {
				dt = (timeDatas[i].getValue(0) - timeDatas[i-1].getValue(pts=1))*0.001d;
				outXf[i][0] += posDatas[i].getEasting(0)*dt/(RC+dt) + outXf[i-1][outXf.length-1]*(RC/(RC+dt));
				outYf[i][0] += posDatas[i].getNorthing(0)*dt/(RC+dt) + outYf[i-1][outYf.length-1]*(RC/(RC+dt));
			}
			
			for(int j=1; j<pts; j++) {
				dt = (timeDatas[i].getValue(j)-timeDatas[i].getValue(j-1)) *0.001d;
				outXf[i][j] += posDatas[i].getEasting(j)*dt/(RC+dt) + outXf[i][j-1]*(RC/(RC+dt));
				outYf[i][j] += posDatas[i].getNorthing(j)*dt/(RC+dt) + outYf[i][j-1]*(RC/(RC+dt));
			}
		}
		
		
		// BACKWARD
		for (int i=posDatas.length-1; i>=0; i--) {
			int pts = posDatas[i].getLength();
//			System.out.println("pts="+pts);
			outXb[i] = new double[pts];
			outYb[i] = new double[pts];
			
			if (i==posDatas.length-1) {
				outXb[i][pts-1]=posDatas[i].getEasting(pts-1);
				outYb[i][pts-1]=posDatas[i].getNorthing(pts-1);
			}else {
				dt = (timeDatas[i+1].getValue(0) - timeDatas[i].getValue(pts=1))*0.001d;
				outXb[i][pts-1] += posDatas[i].getEasting(pts-1)*dt/(RC+dt) + outXb[i+1][0]*(RC/(RC+dt));
				outYb[i][pts-1] += posDatas[i].getNorthing(pts-1)*dt/(RC+dt) + outYb[i+1][0]*(RC/(RC+dt));
			}
			
			for(int j=pts-2; j>=0; j--) {
				dt = (timeDatas[i].getValue(j+1)-timeDatas[i].getValue(j))*0.001d;			
				outXb[i][j] += posDatas[i].getEasting(j)*dt/(RC+dt) + outXb[i][j+1]*(RC/(RC+dt));
				outYb[i][j] += posDatas[i].getNorthing(j)*dt/(RC+dt) + outYb[i][j+1]*(RC/(RC+dt));
			}
		}
		
		
		// MERGE		
		for (int i=0; i<posDatas.length; i++){
				posDatas[i].clear();
				for (int j=0; j<outXb[i].length; j++){
					posDatas[i].addPosition(
							0.5 * (outXf[i][j]+outXb[i][j]),				
							0.5 * (outYf[i][j]+outYb[i][j])
							);
				}
		}
		
	}
}
