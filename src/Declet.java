import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class that represents a signed 10-bit binary number represented using a
 * twos complement representation.
 *
 */
public class Declet implements Comparable<Declet> {

	// Fields
	static final int MAX_VALUE = 511;
	static final int MIN_VALUE = -512;
	static final int NUM_BITS = 10;

	List<Bit> bits;
	
	// Constructors
	public Declet() {
		bits = new ArrayList<Bit>();
		for (int i = 0 ; i < NUM_BITS ; i++) {
			bits.add(Bit.ZERO);
		}
	}
	
	public Declet(Bit... theBits) {
		if (theBits.length != NUM_BITS) {
            throw new IllegalArgumentException("Incorrect number of bits, you require 10 bits for a Declet");
        }
        bits = new ArrayList<Bit>(Arrays.asList(theBits));
	}

	public Declet (int decimal) {
		if (decimal < MIN_VALUE || decimal > MAX_VALUE) {
            throw new IllegalArgumentException("Decimal value out of range");
        }

		bits = new ArrayList<Bit>();

        if (decimal < 0) {
            bits.add(Bit.ONE);
            decimal += 512;
        } 
		
		else {
            bits.add(Bit.ZERO);
        }

        for (int i = 1; i <= NUM_BITS - 1; i++) {
            if (decimal >= Math.pow(2, 9-i)) {
                bits.add(Bit.ONE);
                decimal -= Math.pow(2, 9-i);
            } 
			
			else {
                bits.add(Bit.ZERO);
            }
        }
	}

	// Methods
	public List<Bit> getBits() {
		return new ArrayList<Bit>(bits);
	}
	
	public int toDecimal() {
		int decimal = 0;
		int twosComplement = 1;

		for (int i = NUM_BITS - 1 ; i > 0 ; i--) {
			Bit bit = bits.get(i);
			int bitValue = bit.value();

			decimal += bitValue * twosComplement;
			twosComplement *= 2;
		}
		Bit bit = bits.get(0);
		int bitValue = bit.value();
		if (bitValue == 1) {
			decimal -= bitValue * twosComplement;
		}

		return decimal;
	}

	// @Override
	public String toString() {
		char[] resultList = new char[10];
		for (int i = NUM_BITS - 1 ; i >= 0 ; i--) {
			Bit b = bits.get(i);
			if (b.value() == 0) {
				resultList[i] = '0';
			}
			else {
				resultList[i] = '1';
			}
		}
		String str = new String(resultList);
		return str;
	}

	public boolean isNegative() {
		Bit bit = bits.get(0);
		int bitValue = bit.value();
		return (bitValue == 1);
	}

	public void not() {
		ArrayList<Bit> temp = new ArrayList<Bit>(10);

		for (int i = 0 ; i < NUM_BITS ; i++) {
			Bit bit = bits.get(i);
			temp.add(bit.not());
		}

		bits = temp;
	}

	public void addOne() {

		boolean carry = true;

		Bit signalBit = bits.get(0);
		int signalVal = signalBit.value();

		for (int i = NUM_BITS - 1 ; i >= 0 ; i--) {    
			Bit bit = bits.get(i);
			int bitValue = bit.value();
	
			if (carry == true) {
				if (bitValue == 1) {
					bit = Bit.ZERO;
				} 
				
				else if (bitValue == 0) {
					bit = Bit.ONE;
					carry = false;
				}
				bits.set(i, bit);
			}
		}
	
		if (carry == true) {
			if (signalVal == 0) {
				bits.set(0, Bit.ONE);
			}			
			if (signalVal == 1) {
				bits.set(0, Bit.ZERO);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {

		if (this.equals(obj)) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) { 
			return false;
		}

		Declet other = (Declet) obj; 

		for (int i = 0; i < NUM_BITS; i++) { 
			if (this.bits.get(i) != other.bits.get(i)) {
				return false;
			}
		}

		return true;
	}

	public void add(Declet other) {
		int sum = this.toDecimal() + other.toDecimal();

		if (sum > MAX_VALUE) {
			Declet temp = new Declet(MIN_VALUE + (sum - MAX_VALUE - 1));
			bits = temp.getBits();
		}

		else if (sum < MIN_VALUE) {
			Declet temp = new Declet(MAX_VALUE + (sum - MIN_VALUE + 1));
			bits = temp.getBits();
		}

		else {
			Declet temp = new Declet(sum);
			bits = temp.getBits();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 17;
		int result = 1;
		for (int i = 0; i < NUM_BITS; i++) {
			Bit bit = bits.get(i);
			int bitValue = bit.value();
			result = prime * result + bitValue * (i + 1);
		}
		return result;
	}

	public int compareTo(Declet other) {
		int thisHashCode = this.hashCode();
		int otherHashCode = other.hashCode();
		int result;
		if (thisHashCode < otherHashCode) {
			result = -1;
		} 
		else if (thisHashCode > otherHashCode) {
			result = 1;
		} 
		else {
			result = 0;
		}
		return result;
	}

	/**
	 * Prints some sums illustrating overflow at {@code Declet.MAX_VALUE} and
	 * {@code Declet.MIN_VALUE}.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		Declet d = new Declet(Declet.MAX_VALUE - 2);
		Declet one = new Declet(1);
		
		System.out.println("Overflow at Declet.MAX_VALUE");
		for (int i = 0; i <= 4; i++) {
			System.out.println("d       " + d + " = " + d.toDecimal());
			System.out.println("      + " + one);
			d.addOne();
			System.out.println("d + 1 = " + d + " = " + d.toDecimal());
			System.out.println();
		}
		
		Declet negOne = new Declet(-1);
		
		System.out.println("Overflow at Declet.MIN_VALUE");
		for (int i = 0; i <= 4; i++) {
			System.out.println("d     = " + d + " = " + d.toDecimal());
			System.out.println("      + " + negOne);
			d.add(negOne);
			System.out.println("d - 1 = " + d + " = " + d.toDecimal());
			System.out.println();
		}
	}

}
