/*
 * 
 */
package teme_ai.id3;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;


/**
 * The Class ID3_Algorithm.
 */
public class ID3_Algorithm {

	
	/**
	 * Log2.
	 *
	 * @param num the num
	 * @return the double
	 */
	public static double log2(double num)
	{
		//using convention log2(0) = 0
		if(num == 0)
			return 0;
		
		return (Math.log(num)/Math.log(2));
	} 
	
	/**
	 * Entropy calc.
	 * Number of objects that correspond to a class
	 * @param set the set
	 * @return the double
	 */
	static double entropyCalc(ArrayList<LearningObject> set){
		
		double entropy = 0;
		//determine number of classes
		HashMap<String,Integer> classes = new HashMap<String, Integer>();
		
		for(int i=0;i<set.size();i++){
			
			Integer val = classes.get(set.get(i).classValue.name);
			if(val == null)
				classes.put(set.get(i).classValue.name, 1);
			else
				classes.put(set.get(i).classValue.name,(val+1));	
		}
		
		int totalNo = set.size();
		
		Collection<Integer> values = classes.values();
		
		for(Integer i : values){
			Double v = (double)i;
			//System.out.println(v + " " + totalNo);
			entropy -= ( v/totalNo)*log2((v/totalNo));	
		}	
		
		return entropy;
	}
	
	/**
	 * Entropy calc extended.
	 * Different for numeric and discret ATTRIBUTES
	 * @param posCol the pos col
	 * @param set the set
	 * @return the double
	 */
	static double entropyCalcExt(Attribute attr,ArrayList<LearningObject> set){
		
		//posCol position of interested attribute
		
		Integer posCol = attr.posInLearningSet;
		double entropy = 0;
	
		
		if(attr.type == Attribute.DISCRET){
			
			//determine number of classes
			HashMap<String,ArrayList<LearningObject>> newLearningSet = new HashMap<String, ArrayList<LearningObject>>();
			
			for(int i=0;i<set.size();i++){
			
					ArrayList<LearningObject> val = newLearningSet.get((String)set.get(i).values.get(posCol).getValue());
					
					if(val == null){
						val =new ArrayList<LearningObject>();
						val.add(set.get(i));
						newLearningSet.put(set.get(i).values.get(posCol).valueString, val);
					}
					else{
						val.add(set.get(i));
					}	
				}
			int totalNo = set.size();
			
			
			Set<Entry<String,ArrayList<LearningObject>>> setVal =  newLearningSet.entrySet();
			
			Iterator<Entry<String,ArrayList<LearningObject>>> it = setVal.iterator();
			
			while(it.hasNext()){
				
				Entry<String,ArrayList<LearningObject>> entry =  it.next();
				//System.out.println(entry.getValue().size() +" /" + totalNo + " " + entropyCalc(entry.getValue()) +" dim:"+ entry.getValue().size());
				
				entropy += ( ((double)entry.getValue().size())/totalNo)* entropyCalc(entry.getValue());
			}
		//	System.out.println("- - -");

		}
		else
		{
			// if attribute is NUMERIC and has been DISCRETIZED
			ArrayList<Interval> intervals = attr.numericIntervalBounds;
			int totalNo = set.size();
			
			//System.out.println("Intervals :" + intervals.size());
			for(int i=0;i<intervals.size();i++){
				Interval interval = intervals.get(i);
				//System.out.println("Interval bounds ["+ interval.lowBound +" " + interval.highBound + "]");
				ArrayList<LearningObject> newSet = new ArrayList<LearningObject>();
				
				for(int j=0;j<set.size();j++){
					if(interval.isValueInInterval((Integer)set.get(j).values.get(posCol).getValue()))
						newSet.add(set.get(j));
				}
				
				//in the new set will be attributes in that specific interval
				// newSet size number of learningObject that have numeric attribute in that interval
				// totalNo the total number of learning Pbjects in the set
				if(newSet.size()>0){
					//System.out.println(newSet.size() +" /" + totalNo + " " + entropyCalc(newSet) +" dim:"+ newSet.size());
					
					
					entropy += ((double)newSet.size()/totalNo) * entropyCalc(newSet);
				}	
			}		
		}
		
		return entropy;
	}
	
	/**
	 * Determine most frequent class.
	 *
	 * @param set the set
	 * @return the string
	 */
	public static String determineMostFrequentClass(ArrayList<LearningObject> set){
		
		String fqValue = "notDef";
		
		//determine number of classes
		//System.out.println("Examples in set");
		HashMap<String,Integer> classes = new HashMap<String, Integer>();
				
			for(int i=0;i<set.size();i++){
			//	System.out.println("---set " + set.get(i).toString());	
				Integer val = classes.get(set.get(i).classValue.name);
				if(val == null)
					classes.put(set.get(i).classValue.name, 1);
				else
					classes.put(set.get(i).classValue.name,(val+1));	
				}
				
			Set<Entry<String,Integer>> setVal =  classes.entrySet();
			
			Iterator<Entry<String,Integer>> it = setVal.iterator();
			
			int max = 0;
			while(it.hasNext()){
				Entry<String,Integer> entry =  it.next();
				if(max <= entry.getValue()){
					max = entry.getValue();
					fqValue = entry.getKey();
				}
			}
				
		return fqValue;
	}
	
	
	/**
	 * Determine all object same class.
	 *
	 * @param learningSet the learning set
	 * @return the string
	 */
	private static String determineAllObjectSameClass(ArrayList<LearningObject> learningSet){
		
		String classValue = learningSet.get(0).classValue.name;
		
		for(int i=1; i< learningSet.size();i++){
			if(!classValue.equals(learningSet.get(i).classValue.name))
				return null;
		}
		
		
		return classValue;
		
	}
	/**
	 * Algorithm ID3.
	 *
	 * @param learningSet the learning set
	 * @param attributes the attributes
	 * @param classes the classes
	 * @param nod the nod
	 * @return the nod
	 */
	public static Nod algorithm(ArrayList<LearningObject> learningSet, ArrayList<Attribute> attributes, ArrayList<ClassCategory> classes, Nod nod){
		
		if(learningSet.size() == 0){
			nod.classValue = "Failure";
			return nod;
		}
		
		String onlyOneClass = determineAllObjectSameClass(learningSet);
		
		if(onlyOneClass != null)
		{
			//all learning objects belong to the same class
			nod.classValue = onlyOneClass;
		//	System.out.println("Example in only one class " + onlyOneClass);
			return nod;
		}
		
		
		if(attributes.size() == 0){
			//return node with most frequent class value in learningSet	
			nod.classValue = determineMostFrequentClass(learningSet);
			// soem elementns in this node will be incorrectly classified
			//System.out.println("WORNING some examples could be rongly clasified  in class " + determineMostFrequentClass(learningSet));
			return nod;
		}
		
		//when we do not return a single attribute
		
		//if attribute normal
		
		double max = Double.MIN_VALUE;
		int posChoseAttr = 0;
		
		for(int i=0;i<attributes.size();i++){
			double val = entropyCalc(learningSet) - entropyCalcExt(attributes.get(i), learningSet);
			//System.out.println("Gain " + val +" H(T) " +  entropyCalc(learningSet) + " H(T,X) " +  entropyCalcExt(attributes.get(i), learningSet) + " size learningSet:"+ learningSet.size());
			if(val > max){
				max = val;
				posChoseAttr = i;
			}
		}
		
		//chosen attribute i for separation
		nod.attribute = attributes.get(posChoseAttr);

		//System.out.println("Chosen attribute :"+nod.attribute.name);
		
		//partition sets
		ArrayList<ArrayList<LearningObject>> lSets = new ArrayList<ArrayList<LearningObject>>();
		
		//new attribute set
		ArrayList<Attribute> newAttributeSet = new ArrayList<Attribute>();
		
		//create a new set that excludes the Attribute chosen
		for(int t=0;t<attributes.size();t++)
			if(! attributes.get(t).name.equals(nod.attribute.name))
				newAttributeSet.add(attributes.get(t));
		
		//if attribute is discret type
		if(nod.attribute.type == Attribute.DISCRET){
			
			//connections to other attributes
			ArrayList<String> connections = new ArrayList<String>();
			// For each attribute possible values create a new set that contains that attribute values		 
			for(int i=0;i<nod.attribute.discretValues.size();i++){
				ArrayList<LearningObject> set = new ArrayList<LearningObject>();
				
				for(int j=0;j<learningSet.size();j++){

					int attrPoz = nod.attribute.posInLearningSet;
					
					if(learningSet.get(j).values.get(attrPoz).valueString.equals(nod.attribute.discretValues.get(i)))
						set.add(learningSet.get(j));
				}
				if(set.size()>0){
					//if set is not empty
					lSets.add(set);
					connections.add(nod.attribute.discretValues.get(i));		
				}
			}
			
			nod.tranzitions = connections;
		}
		else
		{
			//numeric attribute
			
			//Numeric attributes are discretizd

			ArrayList<Interval> intervals = nod.attribute.numericIntervalBounds;
			
			if(intervals == null)
				System.out.println("Error, numeric intervals should no be null");	
			ArrayList<Interval> tranzitions = new ArrayList<Interval>();
			
			for(int i=0;i<intervals.size();i++){
				Interval interval = intervals.get(i);
				ArrayList<LearningObject> set = new ArrayList<LearningObject>();
				
				for(int j=0;j<learningSet.size();j++){
					if(interval.isValueInInterval((Integer)learningSet.get(j).values.get(nod.attribute.posInLearningSet).getValue()));
						set.add(learningSet.get(j));
				}
				
				if(set.size()>0){
	
					tranzitions.add(interval);
	
					lSets.add(set);
				}

			}
			
			nod.numericTranzitions = tranzitions;
			
		}
		
		for(int i=0;i<lSets.size();i++){
			Nod new_nod = new Nod();
			//add child node 
			nod.childs.add(new_nod);
			//recursiv call algorithm with new learning set, new attribute set, old classes, and a new nod
			algorithm(lSets.get(i),newAttributeSet, classes, new_nod);
		}
		
		return nod;
	}
	
	
	/**
	 * Read attribute from file.
	 *
	 * @param scan the scan
	 * @param pos the pos in the learningSet
	 * @return the attribute
	 */
	public static Attribute readAttributeFromFile(Scanner scan,int pos){
		
		String name = scan.next();
		
		Attribute attr = new Attribute(name,pos);
		
		String tipAtribut = scan.next();
		int tipA;
		if(tipAtribut.equals("discret"))
			tipA = Attribute.DISCRET;
		else
			tipA = Attribute.NUMERIC;
		
		attr.type = tipA;
		
		
		if(attr.type == Attribute.DISCRET){
			
			int c = scan.nextInt();
			System.out.println(" c : " + c);
			for(int i=0;i<c;i++)
				attr.discretValues.add(scan.next());
		}
		
		return attr;
	}
	
	/**
	 * Discretize numeric attributes process class based one class change.
	 *
	 * @param learningSet the learning set
	 * @param attributes the attributes
	 */
	public static void discretizeNumericAttributesProcessClass(ArrayList<LearningObject> learningSet, ArrayList<Attribute> attributes){
		
		
		for(int i=0; i<attributes.size();i++){
			Attribute attr = attributes.get(i);
			
			if(attr.type == Attribute.NUMERIC){
				//apply discretization process
		
				
				TreeMap<Integer,Association> values = new TreeMap<Integer, Association>();
				//total number of values
				int totalNo = 0;
				//total number of classes
				int totalClasses = 0;
				//intervals to be split in
				ArrayList<Interval> intervals = new ArrayList<Interval>();
				
				for(int j=0;j<learningSet.size();j++){
					Association val = values.get(learningSet.get(j).values.get(attr.posInLearningSet).getValue());
					if(val == null){
						Association assoc = new Association();
						assoc.addClass(learningSet.get(j).classValue.name);
						values.put((Integer)learningSet.get(j).values.get(attr.posInLearningSet).getValue(),assoc);
						totalClasses++;
					}
					else 
						val.addClass(learningSet.get(j).classValue.name);
					totalNo++;
				}

				Interval interval = new Interval();
				Interval oldInterval;
				int index = 0;
				String currentClass = "nodInitialized";
				while(values.size()>0){
					
					Entry<Integer,Association> entry = values.firstEntry();
					
					if(!currentClass.equals(entry.getValue().getDominantClass())){
						
						//System.out.println("Class change from " + currentClass + " to " + entry.getValue().getDominantClass() + " when changed to" + entry.getKey());
						
						//class change
						oldInterval = interval;
						oldInterval.highBound = entry.getKey();
						interval = new Interval();
						interval.lowBound = entry.getKey();
						intervals.add(interval);
						currentClass = entry.getValue().getDominantClass();
					}
					
					if(index == 0){
						interval.lowBound = Integer.MIN_VALUE;
					}
					
					index++;
					values.remove(entry.getKey());
				}
				
				interval.highBound = Integer.MAX_VALUE;
				
				//add intervals to attribute
				attr.numericIntervalBounds = intervals;
				
				System.out.println("Attribute intervals:"+attr.name);
				
				for(int t=0;t<intervals.size();t++)
					System.out.print("[" +intervals.get(t).lowBound+" " + intervals.get(t).highBound+ "]");
				
			}
			
		}
		
		
	}
	
	/**
	 * Discretize numeric attributes process based on buckets.
	 */
	public static void discretizeNumericAttributesProcess(ArrayList<LearningObject> learningSet,ArrayList<Attribute> attributes){
		
		
		int noBuckets = 4;
		
		
		for(int i=0; i<attributes.size();i++){
			Attribute attr = attributes.get(i);
			
			if(attr.type == Attribute.NUMERIC){
				//apply discretization process
		
				TreeMap<Integer,Integer> values = new TreeMap<Integer, Integer>();
				//total number of values
				int totalNo = 0;
				//total number of classes
				int totalClasses = 0;
				//intervals to be split in
				ArrayList<Interval> intervals = new ArrayList<Interval>();
				
				for(int j=0;j<learningSet.size();j++){
					Integer val = values.get(learningSet.get(j).values.get(attr.posInLearningSet).getValue());
					if(val == null){
						values.put((Integer)learningSet.get(j).values.get(attr.posInLearningSet).getValue(),1);
						totalClasses++;
					}
					else 
						values.put((Integer)learningSet.get(j).values.get(attr.posInLearningSet).getValue(),(val+1));		
					totalNo++;
				}
				
				//System.out.println(" totalNo: " + totalNo);
				
				
				// if differentClasses < noBuckets change noBuckets
				if(totalClasses < noBuckets)
					noBuckets = totalClasses;
				
				if(noBuckets == 1){
					//only one interval
					System.out.println("Only one Bucket, not a good training set");
					
				}else{
					
					//get approximate bucket size
					int approximateBucketSize = totalNo/noBuckets;
					
					System.out.println("ApproximateBucketSize"+approximateBucketSize);
					for(int j=0;j<noBuckets;j++){
						
						Interval interval = new Interval();
						
						int size = 0;
						Entry<Integer,Integer> entry = null;
						
						while(size <= approximateBucketSize){
							
							entry = values.firstEntry();
							if(entry != null){
								
								size += entry.getValue();
							//	System.out.println("Adauga " + entry.getKey() + " " + entry.getValue());
								//remove from set
								values.remove(entry.getKey());
							}
							else
								break;
						}
						
						if(entry != null)
							interval.highBound = entry.getKey();
						
						
						//add lower bound in case of first interval or else take high bound from previous interval
						if(j==0)
							interval.lowBound = Integer.MIN_VALUE;
						else
							interval.lowBound = intervals.get((intervals.size()-1)).highBound;

						
						
						//add higher bound to last interval
						if(values.size() == 0 || j == noBuckets -1)
							interval.highBound = Integer.MAX_VALUE;
					//	System.out.println("Intervl" + interval.lowBound + " - " + interval.highBound);
						intervals.add(interval);
					}
				
				}
				//add intervals to attribute
				attr.numericIntervalBounds = intervals;
				
				System.out.println("Attribute intervals:"+attr.name);
				
				for(int t=0;t<intervals.size();t++)
					System.out.print("[" +intervals.get(t).lowBound+" " + intervals.get(t).highBound+ "]");
				
			}
			
		}
		
		
	}
	
	
	/**
	 * Creates the new learning set.
	 *
	 * @param pos the pos
	 * @param learningSet the learning set
	 * @return the array list
	 */
	public static ArrayList<LearningObject> createNewLearningSet(int pos,ArrayList<LearningObject> learningSet){
		
		ArrayList<LearningObject> newLearningSet = new ArrayList<LearningObject>();
		
		for(int i=0;i<learningSet.size();i++){
			ArrayList<AttributeValue> attrValues = new ArrayList<AttributeValue>();
			
			for(int j=0; j<learningSet.get(i).values.size(); j++){
			
				if(pos != j){
					AttributeValue attrVal = new AttributeValue(learningSet.get(i).values.get(j).valueString, learningSet.get(i).values.get(j).type);
					attrValues.add(attrVal);
				}
			}
			AttributeValue attrVal = new AttributeValue(learningSet.get(i).classValue.name, Attribute.DISCRET);
			attrValues.add(attrVal);
			
			//add the value of that attribute as a class
			LearningObject learnObject = new LearningObject(attrValues, new ClassCategory(learningSet.get(i).values.get(pos).getValue().toString()));
		//	System.out.println(learnObject.toString());
			newLearningSet.add(learnObject);
		}
		
		
		return newLearningSet;
	}
	/**
	 * Creates the new classes.
	 *
	 * @param attr the attr
	 * @param learningSet the learning set
	 * @return the array list
	 */
	public static ArrayList<ClassCategory> createNewClasses(Attribute attr,ArrayList<LearningObject> learningSet){
		
		
		ArrayList<ClassCategory> newClasses = new ArrayList<ClassCategory>();
		
		if(attr.type == Attribute.DISCRET){
			HashSet<String> set = new HashSet<String>();
			
			for(int i=0;i<learningSet.size();i++){
				set.add((String)learningSet.get(i).values.get(attr.posInLearningSet).getValue());
			}
			
			Iterator<String> it = set.iterator();
			while(it.hasNext()){
				ClassCategory newClass = new ClassCategory(it.next());
				newClasses.add(newClass);
			}
		}
		else
		{
			HashSet<Integer> set = new HashSet<Integer>();
			
			for(int i=0;i<learningSet.size();i++){
				set.add((Integer)learningSet.get(i).values.get(attr.posInLearningSet).getValue());
			}
			
			Iterator<Integer> it = set.iterator();
			while(it.hasNext()){
				ClassCategory newClass = new ClassCategory(it.next()+"");
				newClasses.add(newClass);
			}
		}
		//create new classes from attribute 
		return newClasses ;
		
	}
	
	/**
	 * Creates the new attribute set.
	 *
	 * @param pos the pos
	 * @param attributes the attributes
	 * @param classes the classes
	 * @return the array list
	 */
	public static ArrayList<Attribute> createNewAttributeSet(int pos, ArrayList<Attribute> attributes,ArrayList<ClassCategory> classes){
		
		//create new sets
				ArrayList<Attribute> newAttributes = new ArrayList<Attribute>();
		
				int index=0;
				//create new attributes
				for(int i=0; i<attributes.size();i++)
					if( i != pos){
						Attribute attr = new Attribute(attributes.get(i).name, index);
						attr.type = attributes.get(i).type;
						attr.discretValues = attributes.get(i).discretValues;
						attr.numericIntervalBounds = attributes.get(i).numericIntervalBounds;
						newAttributes.add(attr);
						index++;
					}
				//create new Attribute from classes
				Attribute attributeClasses = new Attribute("classesAttribute", newAttributes.size());
				attributeClasses.type = Attribute.DISCRET;
				attributeClasses.discretValues = new ArrayList<String>();
				//add discrete values
				for(int i=0;i<classes.size();i++)
					attributeClasses.discretValues.add(classes.get(i).name);
				//add attribute
				newAttributes.add(attributeClasses);
				
				
				return newAttributes;
	}
	
	/**
	 * Discover missing values.
	 *
	 * @param learningSet the learning set
	 * @param attributes the attributes
	 * @param classes the classes
	 */
	public static boolean discoverMissingValues(ArrayList<LearningObject> learningSet, ArrayList<Attribute> attributes, ArrayList<ClassCategory> classes){
		
		//apply algorithm if and only in only one attribute has missing values in learning set
		
		//let the class become ordinary attribute
		//new classes from attribute that has an unknown
		
		//the position of the unknown attribute
		int pos = -2;
		
		//do algorithm to build tree;
		Nod nod = new Nod();
		
		//Find missing attribute 
		for(int i=0;i<learningSet.size();i++){
			for(int j=0; j<learningSet.get(i).values.size(); j++){
				if(learningSet.get(i).values.get(j).isUndefined()){
					pos = j;
					break;
				}
			}
		}
		
		if(pos >= 0){
				
			ArrayList<LearningObject> undefinedSet = new ArrayList<LearningObject>();
			ArrayList<LearningObject> definedSet = new ArrayList<LearningObject>();
			for(int j=0;j<learningSet.size();j++){
				if(learningSet.get(j).values.get(pos).isUndefined()){
					//for the sets that have there value undefineD classify it
					undefinedSet.add(learningSet.get(j));
				}
				else
					definedSet.add(learningSet.get(j));
			}
			
			System.out.println("Pos : " + pos);
			//create new sets
			ArrayList<Attribute> newAttributes = createNewAttributeSet(pos, attributes, classes);
			
			ArrayList<ClassCategory> newClasses = createNewClasses(attributes.get(pos), definedSet);
			
			ArrayList<LearningObject> newLearningSet = createNewLearningSet(pos, definedSet);
			
			ArrayList<LearningObject> findValuesSet = createNewLearningSet(pos, undefinedSet);
			
			//discretizare atribute
			discretizeNumericAttributesProcessClass(newLearningSet, newAttributes);
			
			
			//do algorithm
			algorithm(newLearningSet, newAttributes, newClasses, nod);
			
			//String display = "";
			//printClassificationRules(nod, 0, display);
			
			//System.out.println("Rules should have been showned");
			//find out values by classifying learnObject that has undefined value
			
			ArrayList<String> missingValues = classifySet(nod, findValuesSet);
						
			for(int j=0;j<missingValues.size(); j++){
				System.out.println(" Discovered missing value :" + missingValues.get(j) + ": ");
				undefinedSet.get(j).values.get(pos).setValue(missingValues.get(j));
			}
			
			return true;
		}
			return false;
	}
	
	/**
	 * Prints the classification rules.
	 *
	 * @param nod the nod
	 */
	public static void printClassificationRules(Nod nod,int level,String display){
	
		String pre = "";
		if(level == 0)
			pre = "if ";
		else
			pre = "&& ";
		
			if(nod.attribute != null){
			
				if(nod.attribute.type == Attribute.DISCRET){
			
					for(int i=0;i<nod.childs.size();i++){

						//System.out.print(pre+nod.attribute.name+"=="+nod.tranzitions.get(i)+" ");
						String newDisplay = display + pre+nod.attribute.name+"=="+nod.tranzitions.get(i)+" ";;
						printClassificationRules(nod.childs.get(i),(level+1),newDisplay);
					}
					
				}
				else{
					for(int j=0;j<nod.childs.size();j++){

						//System.out.print(pre +"("+nod.attribute.name+" >" + nod.numericTranzitions.get(j).lowBound +" && " + " "+nod.attribute.name+"<="+nod.numericTranzitions.get(j).highBound+ ")" );
						String newDisplay = display +pre +"("+nod.attribute.name+" >" + nod.numericTranzitions.get(j).lowBound +" && " + " "+nod.attribute.name+"<="+nod.numericTranzitions.get(j).highBound+ ")" ;
						printClassificationRules(nod.childs.get(j),(level+1),newDisplay);
					}
				
				}
			}
			else{	
					//leaf node
					display += "=>" + nod.classValue;
					//System.out.print("=>" + nod.classValue);
					System.out.println(display);
				}
		
	}
	
	
	/**
	 * Classify set.
	 *
	 * @param nod the nod
	 * @param workingSet the working set
	 */
	public static ArrayList<String> classifySet(Nod nod, ArrayList<LearningObject> workingSet){
		
		ArrayList<String> resultClasses = new ArrayList<String>();
		
		System.out.println("Start classify " + workingSet.size());
		for(int i=0; i<workingSet.size();i++){
			
			LearningObject workObject = workingSet.get(i);
			System.out.println(workObject.toString());
			Nod auxNod = nod;
			int stop = 0;
			while(auxNod.attribute != null){
				
			//	System.out.println("Attrbiute" + auxNod.attribute.name);
				
				if(auxNod.attribute.type == Attribute.DISCRET){
					// distret type attribute
					
					for(int j=0; j<auxNod.tranzitions.size();j++){
					//	System.out.print(auxNod.tranzitions.get(j));
						if(auxNod.tranzitions.get(j).equals(workObject.values.get(auxNod.attribute.posInLearningSet).getValue().toString())){
							//	System.out.println("Chosen " + auxNod.tranzitions.get(j) );
								auxNod = auxNod.childs.get(j);
								break;
						}
					}
				}
				else{
					//numeric attribute
					for(int j=0; j<auxNod.numericTranzitions.size();j++){
						if(auxNod.numericTranzitions.get(j).isValueInInterval((Integer)workObject.values.get(auxNod.attribute.posInLearningSet).getValue())){
					//		System.out.println(auxNod.numericTranzitions.get(j).lowBound + " " + auxNod.numericTranzitions.get(j).highBound);
							auxNod = auxNod.childs.get(j);
							break;
						}
					}
				}
			}
			
			//here show class
			System.out.println("Worn object" + i + "+>" +auxNod.classValue);
			resultClasses.add(auxNod.classValue);
			
		}
		
		return resultClasses;
	}
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		
		
		int k = 0;
		int m = 0;
		int n = 0;
		
		ArrayList<ClassCategory> classes = new ArrayList<ClassCategory>(); 
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		ArrayList<LearningObject> learningSet = new ArrayList<LearningObject>();
		ArrayList<LearningObject> testSet = new ArrayList<LearningObject>();
		
		System.out.println(args[0]);
		System.out.println(args[1]);
		//read data from input files
		System.out.println(args[2]);
		
		
		try{
			//attribute
			FileInputStream fstream = new FileInputStream(args[0]); 
			//learning set
			FileInputStream fstream2 = new FileInputStream(args[1]); 
			
			FileInputStream testStream = new FileInputStream(args[2]);
			
			Scanner scan = new Scanner(fstream);
			
			k = scan.nextInt();
			
			System.out.println("k = " + k);
			for(int i=0;i<k;i++){
				String name = scan.next();
				ClassCategory classObject = new ClassCategory(name);
				classes.add(classObject);
			}
			
			m = scan.nextInt();
			System.out.println("m = " + m);
			
			for(int i=0;i<m;i++)
				attributes.add(readAttributeFromFile(scan,i));
		
			scan.close();
			
			Scanner scanSet = new Scanner(fstream2);
			
			n = scanSet.nextInt();

			for(int i=0; i<n; i++){
				
				ArrayList<AttributeValue> values = new ArrayList<AttributeValue>();
				String classValue= "";
				for(int j=0; j<m; j++){
					if(attributes.get(j).type == Attribute.NUMERIC)
						values.add(new AttributeValue(scanSet.next(),Attribute.NUMERIC));
					else
						values.add(new AttributeValue(scanSet.next(),Attribute.DISCRET));
				}
				
				classValue = scanSet.next();
				LearningObject obj = new LearningObject(values,new ClassCategory(classValue));	
				learningSet.add(obj);
				//System.out.println("here");
				
			}
			
			Scanner scanTest = new Scanner(testStream);
			
			int testNo = scanTest.nextInt();
			System.out.println("TestNo : " + testNo);
			
			for(int i=0 ;i<testNo; i++){
				ArrayList<AttributeValue> values = new ArrayList<AttributeValue>();
				for(int j=0; j<m; j++){
					if(attributes.get(j).type == Attribute.NUMERIC)
						values.add(new AttributeValue(scanTest.next(),Attribute.NUMERIC));
					else
						values.add(new AttributeValue(scanTest.next(),Attribute.DISCRET));
				}
				LearningObject lObject = new LearningObject(values, new ClassCategory(""));
				//System.out.println(lObject.toString());
				testSet.add(lObject);
			}
			
		}
		catch(Exception e){
			System.out.println("Exception e : " + e.getMessage());
		}
		
		
		for(int i=0;i<k;i++)
			System.out.print(classes.get(i).name + " ");
		System.out.println("");
		
		for(int j=0;j<m;j++){
			Attribute attr = attributes.get(j);
			System.out.print("Name : " + attr.name + " type: " + attr.type);
			if(attr.type == Attribute.DISCRET){
				System.out.print(" values:" );
				for(int t=0; t<attr.discretValues.size();t++){
					System.out.print(attr.discretValues.get(t) + " ");
				}
				
			}
			System.out.println("");
		}
		
		for(int i=0;i<learningSet.size();i++){
			System.out.print(learningSet.get(i).toString());
			
			System.out.print(learningSet.get(i).classValue.name);
			System.out.println("");
		}
		
		boolean newSetGenerated = discoverMissingValues(learningSet, attributes, classes);
		if(newSetGenerated){
			System.out.println("------------ NEW SET -------------------");
			for(int i= 0; i<learningSet.size();i++){
				System.out.println(learningSet.get(i).toString());
			}
		}
		
		discretizeNumericAttributesProcessClass(learningSet, attributes);
		//root of decision tree
		Nod nod = new Nod();
		algorithm(learningSet, attributes, classes, nod);
		
		printClassificationRules(nod,0,"");
		
		//classifySet(nod, learningSet);
		
		ArrayList<String> result = classifySet(nod, testSet);
		
		System.out.println("clasify result");
		//classify
		for(int i=0; i<result.size();i++)
			System.out.println(result.get(i));
		
		String outFileName = "";
		StringTokenizer str = new StringTokenizer(args[2]);
		String first = str.nextToken(".");
		String ext = str.nextToken(".");
		
		//buid up file name
		if(ext==null){
			outFileName = first+"_out";
		}
		else
		{
			outFileName = first+"_out."+ext;
		}
		try{
			FileWriter fstream = new FileWriter(outFileName);
			BufferedWriter bwriter = new BufferedWriter(fstream);
			bwriter.write(result.size()+"");
			bwriter.newLine();
			for(int i=0; i< result.size();i++){
				bwriter.write(result.get(i));
				bwriter.newLine();
			}
			bwriter.flush();
			bwriter.close();
			fstream.close();
		}catch(Exception e){
			System.out.println("Exceotion in writing to file:"+e.getMessage());
		}
	}
}
