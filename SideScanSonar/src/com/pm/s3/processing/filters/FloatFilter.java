package com.pm.s3.processing.filters;

import com.pm.s3.data.types.FloatData;
import com.pm.s3.data.types.LongData;

public class FloatFilter {

	public static void filterLPTwoWay(FloatData[] floatDatas, LongData[] timeDatas, float freq) {
		float RC = (float)(1.0f/(2.0f*Math.PI*freq));
		float dt;
		
		float[][] outf=new float[floatDatas.length][], outb=new float[floatDatas.length][];
		
		// FORWARD
		for (int i=0; i<floatDatas.length; i++) {
			int pts = floatDatas[i].getLength();
			outf[i] = new float[pts];
			
			if (i==0) {
				outf[0][0]=floatDatas[0].getValue(0);
			}else {
				dt = (timeDatas[i].getValue(0) - timeDatas[i-1].getValue(pts=1))*0.001f;
				outf[i][0] += floatDatas[i].getValue(0)*dt/(RC+dt) + outf[i-1][outf.length-1]*(RC/(RC+dt));
			}
			
			for(int j=1; j<pts; j++) {
				dt = (timeDatas[i].getValue(j)-timeDatas[i].getValue(j-1)) *0.001f;
				outf[i][j] += floatDatas[i].getValue(j)*dt/(RC+dt) + outf[i][j-1]*(RC/(RC+dt));
			}
		}
		
		
		// BACKWARD
		for (int i=floatDatas.length-1; i>=0; i--) {
			int pts = floatDatas[i].getLength();
//			System.out.println("pts="+pts);
			outb[i] = new float[pts];
			
			if (i==floatDatas.length-1) {
				outb[i][pts-1]=floatDatas[i].getValue(pts-1);
			}else {
				dt = (timeDatas[i+1].getValue(0) - timeDatas[i].getValue(pts=1))*0.001f;
				outb[i][pts-1] += floatDatas[i].getValue(pts-1)*dt/(RC+dt) + outb[i+1][0]*(RC/(RC+dt));
			}
			
			for(int j=pts-2; j>=0; j--) {
				dt = (timeDatas[i].getValue(j+1)-timeDatas[i].getValue(j))*0.001f;			
				outb[i][j] += floatDatas[i].getValue(j)*dt/(RC+dt) + outb[i][j+1]*(RC/(RC+dt));
			}
		}
		
		
		// MERGE		
		for (int i=0; i<floatDatas.length; i++){
			floatDatas[i].clear();
				for (int j=0; j<outb[i].length; j++){
					floatDatas[i].addValue(0.5f * (outf[i][j]+outb[i][j]));
				}
		}
		
	}

}
