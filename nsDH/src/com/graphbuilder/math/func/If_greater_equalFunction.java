package com.graphbuilder.math.func;



/**
 * Checks if param0 is greater or equal to param1 if yes, than return param3 else returns param4
 * @author Artur Wojtkowski
 */
public class If_greater_equalFunction implements Function  {

	/**
	If d[0] is greater or equal to d[1] then return value d[2], else return d[3]
	*/
	public double of(double[] d, int numParam) {

                if(d[0]>=d[1]){
                    return d[2];
                }
                else{
                    return d[3];
                }
	}

	/**
	Returns true only for 1 or 2 parameters, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 4;
	}

	public String toString() {
		return "If_greater_equal(x1, x2, true_value, false_value)";
	}

}