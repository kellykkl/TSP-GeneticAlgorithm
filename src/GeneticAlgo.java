import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GeneticAlgo implements ActionListener, Runnable, ChangeListener{

	JFrame frame = new JFrame("TSP with Genetic Algorithm");
	JButton start = new JButton("Start");
	JButton stop = new JButton("Stop");
	JButton reset = new JButton("Reset");
	Container west = new Container();
	JLabel popLabel = new JLabel("Population Size (1-200)", JLabel.CENTER);
	JSlider popsize = new JSlider(1,200,100);
	JLabel eliteLabel = new JLabel("% of Elite population (0-40)", JLabel.CENTER);
	JSlider elite = new JSlider(0,40,30);
	JLabel mutantLabel = new JLabel("% of Mutant population (0-50)", JLabel.CENTER);
	JSlider mutants = new JSlider(0,50,40);
	JLabel biasLabel = new JLabel("Breeding Bias (50%-90%)", JLabel.CENTER);
	JSlider bias = new JSlider(50,90,60);
	JLabel lengthLabel = new JLabel("Length: ", JLabel.CENTER);
	JLabel length = new JLabel("", JLabel.CENTER);
	Container east = new Container();
	public final int map_points = 30;
	private int p=100;
	private double elite_proportion = 0.3;
	private double mutant_proportion = 0.4;
	private double biasedness = 0.6;
	private final int iter=2000;
	public int now_iter = 0;
	boolean running = false;
	ArrayList<Object[]> pop_key = new ArrayList<Object[]>();
	Double[] pop_fitness = new Double[p];
	Map map = new Map(map_points);
	
	public GeneticAlgo() {
		frame.setSize(1000, 600);
		frame.setLayout(new BorderLayout());
		frame.add(map, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		west.setLayout(new GridLayout(3,1));
		west.add(start);
		start.addActionListener(this);
		west.add(stop);
		stop.addActionListener(this);
		west.add(reset);
		reset.addActionListener(this);
		frame.add(west, BorderLayout.WEST);
		
		popsize.addChangeListener(this);
	    popsize.setMinorTickSpacing(50);
	    popsize.setMajorTickSpacing(100);
	    popsize.setPaintTicks(true);
	    //popsize.setPaintLabels(true);
	    elite.addChangeListener(this);
	    elite.setMinorTickSpacing(10);
	    elite.setMajorTickSpacing(20);
	    elite.setPaintTicks(true);
	    //elite.setPaintLabels(true);
	    mutants.addChangeListener(this);
	    mutants.setMinorTickSpacing(10);
	    mutants.setMajorTickSpacing(20);
	    mutants.setPaintTicks(true);
	    //mutants.setPaintLabels(true);
		bias.addChangeListener(this);
	    bias.setMinorTickSpacing(10);
	    bias.setMajorTickSpacing(20);
	    bias.setPaintTicks(true);
	    //bias.setPaintLabels(true);
		east.setLayout(new GridLayout(5,2));
		east.add(popLabel);
		east.add(popsize);
		east.add(eliteLabel);
		east.add(elite);
		east.add(mutantLabel);
		east.add(mutants);
		east.add(biasLabel);
		east.add(bias);
		east.add(lengthLabel);
		east.add(length);
		frame.add(east, BorderLayout.EAST);
		
		
	    		
		    
	}
	
	public static void main(String[] args) {
		new GeneticAlgo();
				
	}
	
	public static ArrayList<Double> init_key(int num_points){
		ArrayList<Double> first_key = new ArrayList<Double>();
		Random r = new Random();
		for (int i=0; i<num_points; i++) {
			first_key.add(r.nextDouble());
		}
		return first_key;
	}
	
	public double get_fitness(Integer[] indexes) {
		double fitness = 0;
		double distancesq;
		ArrayList<Object[]> points = map.getMap();
		for (int i=0; i<indexes.length-1; i++) {
			distancesq = Math.pow((int)points.get(indexes[i+1])[0] - (int)points.get(indexes[i])[0],2)
						+ Math.pow((int)points.get(indexes[i+1])[1] - (int)points.get(indexes[i])[1],2); 
			fitness += Math.pow(distancesq, 0.5); 
		}
		distancesq = Math.pow((int)points.get(indexes[0])[0] - (int)points.get(indexes[0])[0],2)
				+ Math.pow((int)points.get(indexes[indexes.length-1])[1] - (int)points.get(indexes[indexes.length-1])[1],2); 
		fitness += Math.pow(distancesq, 0.5);		
		
		return fitness;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(start)) {
			//Initialize a population (size p) of keys
			for (int i=0; i<p; i++) {
				ArrayList<Double> curr_key = init_key(map_points);
				pop_key.add(curr_key.toArray());			
			}
			pop_fitness = new Double[p];
			if (running == false) {
				running = true;
				Thread t = new Thread(this);
				t.start();
			}
		}
		if (event.getSource().equals(stop)) {
			running = false;
		}
		if (event.getSource().equals(reset)) {
			now_iter = 0;
			for (int i=0; i<p; i++) {
				ArrayList<Double> curr_key = init_key(map_points);
				pop_key.set(i,curr_key.toArray());			
			}
			for (int i=0; i<map.list_points.size(); i++) {
				//System.out.println(Arrays.toString(list_points.get(i)));	
//				System.out.println(list_points.get(i)[0]);
				map.indexes[i] = 0;
			}
			frame.repaint();
		}
		
	}

	@Override
	public void run() {
		while (running == true) {
			step();
			frame.repaint();
			try {
				Thread.sleep(50);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void step() {
		
		if (now_iter < iter) {
			for (int i=0; i<p; i++) {
				
				//cast ArrayList<Double> to Double[]
				Double[] target = new Double[map_points];
				for (int j = 0; j < target.length; j++) {
				   target[j] = (Double) pop_key.get(i)[j];                
				}
				
			    Decoder comparator = new Decoder(target);
			    Integer[] indexes = comparator.createIndexArray();
			    Arrays.sort(indexes, comparator);
			    //System.out.println(Arrays.toString(indexes));
			    pop_fitness[i] = get_fitness(indexes);		    
			}
		    
		    //Can also use decoder here (0 is shortest distance, fittest)
		    Decoder comparator1 = new Decoder(pop_fitness);
		    Integer[] fitness_index = comparator1.createIndexArray();
		    Arrays.sort(fitness_index, comparator1);
		    //System.out.println(Arrays.toString(pop_fitness));
		    //System.out.println(Arrays.toString(fitness_index));
		    
		    
		    //partition population into elite and non-elite
		    int elite_up_to = (int) Math.round(elite_proportion*p);
		    ArrayList<Object[]> elite_pop = new ArrayList<Object[]>();
		    ArrayList<Object[]> non_elite_pop = new ArrayList<Object[]>();
			Double[] fittest = new Double[map_points];
		    
		    
		    for (int i=0; i<elite_up_to; i++) {
		    	elite_pop.add(pop_key.get(fitness_index[i]));
		    	if (i==0) {
	    			System.out.println(pop_fitness[fitness_index[i]]);
	    			length.setText(String.valueOf(pop_fitness[fitness_index[i]]));
		    		for (int j=0; j<map_points; j++) {
		    			fittest[j] = (Double) pop_key.get(fitness_index[0])[j];
		    		}
		    	}
		    }
		    for (int i=elite_up_to; i<p; i++) {
		    	non_elite_pop.add(pop_key.get(fitness_index[i]));
		    }
		    

		    //plot best solution
		    Decoder comparator2 = new Decoder(fittest);
		    Integer[] best_route = comparator2.createIndexArray();
		    Arrays.sort(best_route, comparator2);
		    //System.out.println(Arrays.toString(best_route));	    
		    map.setSolution(best_route);
			frame.repaint();	    
		    
		    
		    //add all elites to next generation
		   for (int i=0; i<elite_pop.size(); i++) {
			   pop_key.set(i, elite_pop.get(i));
		   }
		   
		   //add mutants
		   int no_mutants = (int) Math.round(mutant_proportion*p);
		   for (int i=0; i<no_mutants; i++) {
			   ArrayList<Double> mutant = init_key(map_points);
			   pop_key.set(i+elite_pop.size(), mutant.toArray());
		   }
		   
		   //add children
		   for (int i=elite_pop.size() + no_mutants; i<pop_key.size(); i++) {
			   //choose parent from elite pop at random
			   Random r = new Random();
			   ArrayList<Double> child = new ArrayList<Double>();
			   Object[] parent_elite = elite_pop.get(r.nextInt(elite_pop.size()));
			   //choose parent from non-elite pop at random
			   Object[] parent_nonelite = non_elite_pop.get(r.nextInt(non_elite_pop.size()));
			   //mate
			   for (int j=0; j<map_points; j++) {
				   boolean elite = (r.nextDouble() < biasedness);
				   if (elite == true) {
					   child.add((Double) parent_elite[j]);
				   }else {
					   child.add((Double) parent_nonelite[j]);
				   }
			   }
			   pop_key.set(i, child.toArray());
		   }
		   
		   now_iter += 1;
		}

	   
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource().equals(popsize)) {
		    JSlider source = (JSlider)event.getSource();
		    if (!source.getValueIsAdjusting()) {
		        p = (int)source.getValue();
		        System.out.println(p);			
		    }
	    }
		if (event.getSource().equals(elite)) {
		    JSlider source2 = (JSlider)event.getSource();
		    if (!source2.getValueIsAdjusting()) {
		        elite_proportion = (double)source2.getValue()/100;
		        System.out.println(elite_proportion);			
		    }
	    }
		if (event.getSource().equals(bias)) {
		    JSlider source3 = (JSlider)event.getSource();
		    if (!source3.getValueIsAdjusting()) {
		        biasedness = (double)source3.getValue()/100;
		        System.out.println(biasedness);			
		    }
	    }
		if (event.getSource().equals(mutants)) {
		    JSlider source3 = (JSlider)event.getSource();
		    if (!source3.getValueIsAdjusting()) {
		        mutant_proportion = (double)source3.getValue()/100;
		        System.out.println(mutant_proportion);			
		    }
	    }
		
	}
	
	

}
