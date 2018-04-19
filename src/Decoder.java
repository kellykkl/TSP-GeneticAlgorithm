import java.util.Arrays;
import java.util.Comparator;

public class Decoder implements Comparator<Integer>{

	private final Double[] array;
	
    public Decoder(Double[] array)
    {
        this.array = array;
    }
	
    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
        {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }
    
    
    @Override
    public int compare(Integer index1, Integer index2)
    {
         // Autounbox from Integer to int to use as array indexes
        //return array[index1].compareTo(array[index2]);
    	if (array[index1]>array[index2]) {
    		return 1;
    	}else if (array[index1]<array[index2]){
    		return -1;
    	}else {
    		return 0;
    	}
    }
    
    
    //to use:
    //Double[] key = { 0.5,0.3, ... };
    //Decoder comparator = new Decoder(key);
    //Integer[] indexes = comparator.createIndexArray();
    //Arrays.sort(indexes, comparator);
    // Now the indexes are in appropriate order.

}
