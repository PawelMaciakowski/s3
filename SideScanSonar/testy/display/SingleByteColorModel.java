package display;

import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;

public class SingleByteColorModel extends ComponentColorModel {


	public SingleByteColorModel(ColorSpace arg0, int[] arg1, boolean arg2,
			boolean arg3, int arg4, int arg5) {
		super(arg0, arg1, arg2, arg3, arg4, arg5);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getAlpha(int arg0) {
		System.out.println("a");
		return 0;
	}

	@Override
	public int getBlue(int arg0) {
		System.out.println("b");
		return super.getBlue(arg0);
	}

	@Override
	public int getGreen(int arg0) {
		System.out.println("g");
		return 0;
	}

	@Override
	public int getRed(int arg0) {
		System.out.println("r");
		return 0;
	}
	
	public int getRGB(int pix){
		System.out.println("rgb");
		return 0;
	}
	
	public int getColor(int b){
		b = 255-b;
		boolean th = b<160;
		int r = th?(int)(255 - 0.40635*b):(-2*b+510);
		int g = th?(int)(255-1.40625*b):(int)(-0.315789*b+80.5263);
		return 0xff000000 + r*256*256 + g*256;
	}
	
	public int getRGB(Object inData){
		return getColor((int) (((byte[]) inData)[0] & 0xff));
	}
}
