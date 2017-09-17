package display;

import java.awt.color.ColorSpace;

public class SingleByteColorSpace extends ColorSpace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SingleByteColorSpace() {
		super(ColorSpace.TYPE_GRAY, 1);
	}
	
	public float getMaxValue(int c){
		System.out.println("max");
		return 360.0f;
	}
	
	public float getMinValue(int c){
		System.out.println("min");
		return 0;
	}

	@Override
	public float[] fromCIEXYZ(float[] arg0) {
		// TODO Auto-generated method stub
		System.out.println("1");
		return null;
	}

	@Override
	public float[] fromRGB(float[] arg0) {
		// TODO Auto-generated method stub
		System.out.println("2");
		return null;
	}

	@Override
	public float[] toCIEXYZ(float[] arg0) {
		// TODO Auto-generated method stub
		System.out.println("3");
		return null;
	}

	@Override
	public float[] toRGB(float[] arg0) {
		// TODO Auto-generated method stub
		System.out.println("4");
		return null;
	}

}
