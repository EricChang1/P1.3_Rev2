package models;

import java.util.ArrayList;

import models.BasicShape.RotationDir;

public class MakePieces {
	
	public static ArrayList<Block> getA()
	{
		ArrayList<Block> listA = new ArrayList<Block>();
		BasicShape a = Container.constructInitShape(2, 2, 4);
		Block A = new Block (a, 1); 
		listA.add(A);
		Matrix<Double> rotY = BasicShape.rotationMatrix(90, 0, RotationDir.ONWARD);
		Matrix<Double> rotZ = BasicShape.rotationMatrix(0, 90, RotationDir.ONWARD);
		
		Block A1 = A.clone(); A1.rotate(rotY);
		Block A2 = A.clone(); A2.rotate(rotZ); A2.rotate(rotY);A2.rotate(rotZ);
		
		BasicShape aa1 = Container.constructInitShape(A1.getDimensions(0), A1.getDimensions(1), A1.getDimensions(2));
		BasicShape aa2 = Container.constructInitShape(A2.getDimensions(0), A2.getDimensions(1), A2.getDimensions(2));
		Block AA1 = new Block(aa1,1); listA.add(AA1);
		Block AA2 = new Block(aa2,1); listA.add(AA2);
		
		return listA;
	}

	public static ArrayList<Block> getB()
	{
		ArrayList<Block> listB = new ArrayList<Block>();
		BasicShape b = Container.constructInitShape(2, 3, 4);
		Block B = new Block (b, 1); 
		listB.add(B);
		Matrix<Double> rotY = BasicShape.rotationMatrix(90, 0, RotationDir.ONWARD);
		Matrix<Double> rotZ = BasicShape.rotationMatrix(0, 90, RotationDir.ONWARD);
		Block B1 = B.clone(); B1.rotate(rotY);
		Block B2 = B.clone(); B2.rotate(rotZ); B2.rotate(rotY);B2.rotate(rotZ);
		
		BasicShape bb1 = Container.constructInitShape(B1.getDimensions(0), B1.getDimensions(1), B1.getDimensions(2));
		BasicShape bb2 = Container.constructInitShape(B2.getDimensions(0), B2.getDimensions(1), B2.getDimensions(2));
		Block BB1 = new Block(bb1,1); listB.add(BB1);
		Block BB2 = new Block(bb2,1); listB.add(BB2);
		
		
		return listB;
	}

	public static ArrayList<Block> getC()
	{
		ArrayList<Block> listC = new ArrayList<Block>();
		BasicShape c = Container.constructInitShape(3, 3, 3);
		Block C = new Block (c, 1); 
		listC.add(C);
		return listC;
	}
	
	public static ArrayList<Block> getL()
	{
		Matrix<Double> rotY = BasicShape.rotationMatrix(90, 0, RotationDir.ONWARD);
		Matrix<Double> rotZ = BasicShape.rotationMatrix(0, 90, RotationDir.ONWARD);
		ArrayList<Block> listL = new ArrayList<Block>();
		
		Block L1 = Pieces.createLBlock(); listL.add(L1);
		Block L2 = L1.clone(); L2.rotate(rotZ);listL.add(L2);
		Block L3 = L2.clone(); L3.rotate(rotZ);listL.add(L3);
		Block L4 = L3.clone(); L4.rotate(rotZ);listL.add(L4);
		Block L5 = L1.clone(); L5.rotate(rotY);listL.add(L5);
		Block L6 = L5.clone(); L6.rotate(rotZ);listL.add(L6);
		Block L7 = L6.clone(); L7.rotate(rotZ);listL.add(L7);
		Block L8 = L7.clone(); L8.rotate(rotZ);listL.add(L8);
		Block L9 = L5.clone(); L9.rotate(rotY);listL.add(L9);
		Block L10 = L9.clone(); L10.rotate(rotZ);listL.add(L10);
		Block L11 = L10.clone(); L11.rotate(rotZ);listL.add(L11);
		Block L12 = L11.clone(); L12.rotate(rotZ);listL.add(L12);
		Block L13 = L9.clone(); L13.rotate(rotY);listL.add(L13);
		Block L14 = L13.clone(); L14.rotate(rotZ);listL.add(L14);
		Block L15 = L14.clone(); L15.rotate(rotZ);listL.add(L15);
		Block L16 = L15.clone(); L16.rotate(rotZ);listL.add(L16);
		
		return listL;
	}
	
	public static ArrayList<Position> getLPos()
	{
		ArrayList<Position> posL = new ArrayList<Position>();
		ArrayList <Integer> lp1= new ArrayList <Integer> (); 		lp1.add(0); lp1.add(0); lp1.add(0); 		Position LP1 = new Position(lp1); posL.add(LP1);
		ArrayList <Integer> lp2= new ArrayList <Integer> (); 		lp2.add(4); lp2.add(0); lp2.add(0); 		Position LP2 = new Position(lp2); posL.add(LP2);
		ArrayList <Integer> lp3= new ArrayList <Integer> (); 		lp3.add(2); lp3.add(4); lp3.add(0); 		Position LP3 = new Position(lp3); posL.add(LP3);
		ArrayList <Integer> lp4= new ArrayList <Integer> (); 		lp4.add(0); lp4.add(2); lp4.add(0); 		Position LP4 = new Position(lp4); posL.add(LP4);
		ArrayList <Integer> lp5= new ArrayList <Integer> (); 		lp5.add(0); lp5.add(0); lp5.add(2); 		Position LP5 = new Position(lp5); posL.add(LP5);
		ArrayList <Integer> lp6= new ArrayList <Integer> (); 		lp6.add(4); lp6.add(0); lp6.add(2); 		Position LP6 = new Position(lp6); posL.add(LP6);
		ArrayList <Integer> lp7= new ArrayList <Integer> (); 		lp7.add(1); lp7.add(4); lp7.add(2); 		Position LP7 = new Position(lp7); posL.add(LP7);
		ArrayList <Integer> lp8= new ArrayList <Integer> (); 		lp8.add(0); lp8.add(1); lp8.add(2); 		Position LP8 = new Position(lp8); posL.add(LP8);
		ArrayList <Integer> lp9= new ArrayList <Integer> (); 		lp9.add(2); lp9.add(0); lp9.add(1); 		Position LP9 = new Position(lp9); posL.add(LP9);
		ArrayList <Integer> lp10= new ArrayList <Integer> (); 		lp10.add(4); lp10.add(2); lp10.add(1); 		Position LP10 = new Position(lp10); posL.add(LP10);
		ArrayList <Integer> lp11= new ArrayList <Integer> (); 		lp11.add(0); lp11.add(4); lp11.add(1); 		Position LP11 = new Position(lp11); posL.add(LP11);
		ArrayList <Integer> lp12= new ArrayList <Integer> (); 		lp12.add(0); lp12.add(0); lp12.add(1); 		Position LP12 = new Position(lp12); posL.add(LP12);
		ArrayList <Integer> lp13= new ArrayList <Integer> (); 		lp13.add(1); lp13.add(0); lp13.add(0); 		Position LP13 = new Position(lp13); posL.add(LP13);
		ArrayList <Integer> lp14= new ArrayList <Integer> (); 		lp14.add(4); lp14.add(1); lp14.add(0); 		Position LP14 = new Position(lp14); posL.add(LP14);
		ArrayList <Integer> lp15= new ArrayList <Integer> (); 		lp15.add(0); lp15.add(4); lp15.add(0); 		Position LP15 = new Position(lp15); posL.add(LP15);
		ArrayList <Integer> lp16= new ArrayList <Integer> (); 		lp16.add(0); lp16.add(0); lp16.add(0); 		Position LP16 = new Position(lp16); posL.add(LP16);
		return posL;
	}
	
	public static ArrayList<Block> getP()
	{
		Matrix<Double> rotY = BasicShape.rotationMatrix(90, 0, RotationDir.ONWARD);
		Matrix<Double> rotZ = BasicShape.rotationMatrix(0, 90, RotationDir.ONWARD);
		ArrayList<Block> listP = new ArrayList<Block>();
		
		Block P1 = Pieces.createPBlock(); listP.add(P1);
		Block P2 = P1.clone(); P2.rotate(rotZ);listP.add(P2);
		Block P3 = P2.clone(); P3.rotate(rotZ);listP.add(P3);
		Block P4 = P3.clone(); P4.rotate(rotZ);listP.add(P4);
		Block P5 = P1.clone(); P5.rotate(rotY);listP.add(P5);
		Block P6 = P5.clone(); P6.rotate(rotZ);listP.add(P6);
		Block P7 = P6.clone(); P7.rotate(rotZ);listP.add(P7);
		Block P8 = P7.clone(); P8.rotate(rotZ);listP.add(P8);
		Block P9 = P5.clone(); P9.rotate(rotY);listP.add(P9);
		Block P10 = P9.clone(); P10.rotate(rotZ);listP.add(P10);
		Block P11 = P10.clone(); P11.rotate(rotZ);listP.add(P11);
		Block P12 = P11.clone(); P12.rotate(rotZ);listP.add(P12);
		Block P13 = P9.clone(); P13.rotate(rotY);listP.add(P13);
		Block P14 = P13.clone(); P14.rotate(rotZ);listP.add(P14);
		Block P15 = P14.clone(); P15.rotate(rotZ);listP.add(P15);
		Block P16 = P15.clone(); P16.rotate(rotZ);listP.add(P16);
		
		return listP;
	}
	
	
	public static ArrayList<Position> getPPos()
	{
		ArrayList<Position> posP = new ArrayList<Position>();
		ArrayList <Integer> pp1= new ArrayList <Integer> (); 		pp1.add(0); pp1.add(0); pp1.add(0); 		Position PP1 = new Position(pp1); posP.add(PP1);
		ArrayList <Integer> pp2= new ArrayList <Integer> (); 		pp2.add(3); pp2.add(0); pp2.add(0); 		Position PP2 = new Position(pp2); posP.add(PP2);
		ArrayList <Integer> pp3= new ArrayList <Integer> (); 		pp3.add(2); pp3.add(3); pp3.add(0); 		Position PP3 = new Position(pp3); posP.add(PP3);
		ArrayList <Integer> pp4= new ArrayList <Integer> (); 		pp4.add(0); pp4.add(2); pp4.add(0); 		Position PP4 = new Position(pp4); posP.add(PP4);
		ArrayList <Integer> pp5= new ArrayList <Integer> (); 		pp5.add(0); pp5.add(0); pp5.add(2); 		Position PP5 = new Position(pp5); posP.add(PP5);
		ArrayList <Integer> pp6= new ArrayList <Integer> (); 		pp6.add(3); pp6.add(0); pp6.add(2); 		Position PP6 = new Position(pp6); posP.add(PP6);
		ArrayList <Integer> pp7= new ArrayList <Integer> (); 		pp7.add(1); pp7.add(3); pp7.add(2); 		Position PP7 = new Position(pp7); posP.add(PP7);
		ArrayList <Integer> pp8= new ArrayList <Integer> (); 		pp8.add(0); pp8.add(1); pp8.add(2); 		Position PP8 = new Position(pp8); posP.add(PP8);
		ArrayList <Integer> pp9= new ArrayList <Integer> (); 		pp9.add(2); pp9.add(0); pp9.add(1); 		Position PP9 = new Position(pp9); posP.add(PP9);
		ArrayList <Integer> pp10= new ArrayList <Integer> (); 		pp10.add(3); pp10.add(2); pp10.add(1); 		Position PP10 = new Position(pp10); posP.add(PP10);
		ArrayList <Integer> pp11= new ArrayList <Integer> (); 		pp11.add(0); pp11.add(3); pp11.add(1); 		Position PP11 = new Position(pp11); posP.add(PP11);
		ArrayList <Integer> pp12= new ArrayList <Integer> (); 		pp12.add(0); pp12.add(0); pp12.add(1); 		Position PP12 = new Position(pp12); posP.add(PP12);
		ArrayList <Integer> pp13= new ArrayList <Integer> (); 		pp13.add(1); pp13.add(0); pp13.add(0); 		Position PP13 = new Position(pp13); posP.add(PP13);
		ArrayList <Integer> pp14= new ArrayList <Integer> (); 		pp14.add(3); pp14.add(1); pp14.add(0); 		Position PP14 = new Position(pp14); posP.add(PP14);
		ArrayList <Integer> pp15= new ArrayList <Integer> (); 		pp15.add(0); pp15.add(3); pp15.add(0); 		Position PP15 = new Position(pp15); posP.add(PP15);
		ArrayList <Integer> pp16= new ArrayList <Integer> (); 		pp16.add(0); pp16.add(0); pp16.add(0); 		Position PP16 = new Position(pp16); posP.add(PP16);
		return posP;
	}
	
	public static ArrayList<Block> getT()
	{
		Matrix<Double> rotY = BasicShape.rotationMatrix(90, 0, RotationDir.ONWARD);
		Matrix<Double> rotZ = BasicShape.rotationMatrix(0, 90, RotationDir.ONWARD);
		ArrayList<Block> listT = new ArrayList<Block>();
		Block T1 = Pieces.createTBlock(); listT.add(T1);
		Block T2 = T1.clone(); T2.rotate(rotZ);listT.add(T2);
		Block T3 = T2.clone(); T3.rotate(rotZ);listT.add(T3);
		Block T4 = T3.clone(); T4.rotate(rotZ);listT.add(T4);
		Block T5 = T1.clone(); T5.rotate(rotY);listT.add(T5);
		Block T6 = T5.clone(); T6.rotate(rotZ);listT.add(T6);
		Block T7 = T6.clone(); T7.rotate(rotZ);listT.add(T7);
		Block T8 = T7.clone(); T8.rotate(rotZ);listT.add(T8);
		Block T9 = T1.clone(); T9.rotate(rotY);T9.rotate(rotZ);	T9.rotate(rotY);T9.rotate(rotZ);listT.add(T9);
		Block T10 = T9.clone(); T10.rotate(rotZ);listT.add(T10);
		Block T11 = T10.clone(); T11.rotate(rotZ);T11.rotate(rotY);T11.rotate(rotY);listT.add(T11);
		Block T12 = T11.clone(); T12.rotate(rotZ);listT.add(T12);
		
		return listT;
	}
	
	public static ArrayList<Position> getTPos()
	{
		ArrayList<Position> posT = new ArrayList<Position>();
		ArrayList <Integer> tt1= new ArrayList <Integer> (); 		tt1.add(0); tt1.add(0); tt1.add(0); 		Position TT1 = new Position(tt1); posT.add(TT1);
		ArrayList <Integer> tt2= new ArrayList <Integer> (); 		tt2.add(3); tt2.add(0); tt2.add(0); 		Position TT2 = new Position(tt2); posT.add(TT2);
		ArrayList <Integer> tt3= new ArrayList <Integer> (); 		tt3.add(3); tt3.add(3); tt3.add(0); 		Position TT3 = new Position(tt3); posT.add(TT3);
		ArrayList <Integer> tt4= new ArrayList <Integer> (); 		tt4.add(0); tt4.add(3); tt4.add(0); 		Position TT4 = new Position(tt4); posT.add(TT4);
		ArrayList <Integer> tt5= new ArrayList <Integer> (); 		tt5.add(0); tt5.add(0); tt5.add(3); 		Position TT5 = new Position(tt5); posT.add(TT5);
		ArrayList <Integer> tt6= new ArrayList <Integer> (); 		tt6.add(3); tt6.add(0); tt6.add(3); 		Position TT6 = new Position(tt6); posT.add(TT6);
		ArrayList <Integer> tt7= new ArrayList <Integer> (); 		tt7.add(1); tt7.add(3); tt7.add(3); 		Position TT7 = new Position(tt7); posT.add(TT7);
		ArrayList <Integer> tt8= new ArrayList <Integer> (); 		tt8.add(0); tt8.add(1); tt8.add(3); 		Position TT8 = new Position(tt8); posT.add(TT8);
		ArrayList <Integer> tt9= new ArrayList <Integer> (); 		tt9.add(1); tt9.add(3); tt9.add(0); 		Position TT9 = new Position(tt9); posT.add(TT9);
		ArrayList <Integer> tt10= new ArrayList <Integer> (); 		tt10.add(0); tt10.add(1); tt10.add(0); 		Position TT10 = new Position(tt10); posT.add(TT10);
		ArrayList <Integer> tt11= new ArrayList <Integer> (); 		tt11.add(1); tt11.add(0); tt11.add(3); 		Position TT11 = new Position(tt11); posT.add(TT11);
		ArrayList <Integer> tt12= new ArrayList <Integer> (); 		tt12.add(3); tt12.add(1); tt12.add(3); 		Position TT12 = new Position(tt12); posT.add(TT12);

		return posT;
	}
	
	public static ArrayList<ArrayList<ArrayList<Integer>>> getLFilled()
	{
		ArrayList<ArrayList<ArrayList<Integer>>> list = new ArrayList<ArrayList<ArrayList<Integer>>>();
		ArrayList<ArrayList<Integer>> one = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp1= new ArrayList <Integer> (); 		lp1.add(0); lp1.add(0); lp1.add(0); 	one.add(lp1);
		ArrayList <Integer> lp2= new ArrayList <Integer> (); 		lp2.add(0); lp2.add(1); lp2.add(0); 	one.add(lp2);	
		ArrayList <Integer> lp3= new ArrayList <Integer> (); 		lp3.add(0); lp3.add(2); lp3.add(0); 	one.add(lp3);	
		ArrayList <Integer> lp4= new ArrayList <Integer> (); 		lp4.add(0); lp4.add(3); lp4.add(0); 	one.add(lp4);
		ArrayList <Integer> lp5= new ArrayList <Integer> (); 		lp5.add(1); lp5.add(0); lp5.add(0); 	one.add(lp5); list.add(one);
		
		
		ArrayList<ArrayList<Integer>> two = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp6= new ArrayList <Integer> (); 		lp6.add(0); lp6.add(0); lp6.add(0); 	two.add(lp6);
		ArrayList <Integer> lp7= new ArrayList <Integer> (); 		lp7.add(1); lp7.add(0); lp7.add(0); 	two.add(lp7);
		ArrayList <Integer> lp8= new ArrayList <Integer> (); 		lp8.add(2); lp8.add(0); lp8.add(0); 	two.add(lp8);	
		ArrayList <Integer> lp9= new ArrayList <Integer> (); 		lp9.add(3); lp9.add(0); lp9.add(0); 	two.add(lp9);	
		ArrayList <Integer> lp10= new ArrayList <Integer> (); 		lp10.add(3); lp10.add(1); lp10.add(0);  two.add(lp10); list.add(two);
		
		
		ArrayList<ArrayList<Integer>> three = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp11= new ArrayList <Integer> (); 		lp11.add(0); lp11.add(3); lp11.add(0); 	three.add(lp11);
		ArrayList <Integer> lp12= new ArrayList <Integer> (); 		lp12.add(1); lp12.add(0); lp12.add(0); 	three.add(lp12);	
		ArrayList <Integer> lp13= new ArrayList <Integer> (); 		lp13.add(1); lp13.add(1); lp13.add(0); 	three.add(lp13);	
		ArrayList <Integer> lp14= new ArrayList <Integer> (); 		lp14.add(1); lp14.add(2); lp14.add(0); 	three.add(lp14);	
		ArrayList <Integer> lp15= new ArrayList <Integer> (); 		lp15.add(1); lp15.add(3); lp15.add(0);  three.add(lp15); list.add(three);
		
		ArrayList<ArrayList<Integer>> four = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp16= new ArrayList <Integer> (); 		lp16.add(0); lp16.add(0); lp16.add(0); 	four.add(lp16);
		ArrayList <Integer> lp17= new ArrayList <Integer> (); 		lp17.add(0); lp17.add(1); lp17.add(0); 	four.add(lp17);	
		ArrayList <Integer> lp18= new ArrayList <Integer> (); 		lp18.add(1); lp18.add(1); lp18.add(0); 	four.add(lp18);	
		ArrayList <Integer> lp19= new ArrayList <Integer> (); 		lp19.add(2); lp19.add(1); lp19.add(0); 	four.add(lp19);	
		ArrayList <Integer> lp20= new ArrayList <Integer> (); 		lp20.add(3); lp20.add(1); lp20.add(0);  four.add(lp20); list.add(four);
		
		ArrayList<ArrayList<Integer>> five = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp21= new ArrayList <Integer> (); 		lp21.add(0); lp21.add(0); lp21.add(0); 	five.add(lp21);
		ArrayList <Integer> lp22= new ArrayList <Integer> (); 		lp22.add(0); lp22.add(0); lp22.add(1); 	five.add(lp22);	
		ArrayList <Integer> lp23= new ArrayList <Integer> (); 		lp23.add(0); lp23.add(1); lp23.add(1); 	five.add(lp23);	
		ArrayList <Integer> lp24= new ArrayList <Integer> (); 		lp24.add(0); lp24.add(2); lp24.add(1); 	five.add(lp24);	
		ArrayList <Integer> lp25= new ArrayList <Integer> (); 		lp25.add(0); lp25.add(3); lp25.add(1);  five.add(lp25); list.add(five);
		
		ArrayList<ArrayList<Integer>> six = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp26= new ArrayList <Integer> (); 		lp26.add(3); lp26.add(0); lp26.add(0); 	six.add(lp26);
		ArrayList <Integer> lp27= new ArrayList <Integer> (); 		lp27.add(0); lp27.add(0); lp27.add(1); 	six.add(lp27);	
		ArrayList <Integer> lp28= new ArrayList <Integer> (); 		lp28.add(1); lp28.add(0); lp28.add(1); 	six.add(lp28);	
		ArrayList <Integer> lp29= new ArrayList <Integer> (); 		lp29.add(2); lp29.add(0); lp29.add(1); 	six.add(lp29);	
		ArrayList <Integer> lp30= new ArrayList <Integer> (); 		lp30.add(3); lp30.add(0); lp30.add(1);  six.add(lp30); list.add(six);
		
		ArrayList<ArrayList<Integer>> seven = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp31= new ArrayList <Integer> (); 		lp31.add(0); lp31.add(0); lp31.add(1); 	seven.add(lp31);
		ArrayList <Integer> lp32= new ArrayList <Integer> (); 		lp32.add(0); lp32.add(3); lp32.add(1); 	seven.add(lp32);	
		ArrayList <Integer> lp33= new ArrayList <Integer> (); 		lp33.add(0); lp33.add(1); lp33.add(1); 	seven.add(lp33);	
		ArrayList <Integer> lp34= new ArrayList <Integer> (); 		lp34.add(0); lp34.add(2); lp34.add(1); 	seven.add(lp34);	
		ArrayList <Integer> lp35= new ArrayList <Integer> (); 		lp35.add(0); lp35.add(3); lp35.add(0);  seven.add(lp35); list.add(seven);
		
		ArrayList<ArrayList<Integer>> eight = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp36= new ArrayList <Integer> (); 		lp36.add(0); lp36.add(0); lp36.add(0); 	eight.add(lp36);
		ArrayList <Integer> lp37= new ArrayList <Integer> (); 		lp37.add(0); lp37.add(0); lp37.add(1); 	eight.add(lp37);	
		ArrayList <Integer> lp38= new ArrayList <Integer> (); 		lp38.add(1); lp38.add(0); lp38.add(1); 	eight.add(lp38);	
		ArrayList <Integer> lp39= new ArrayList <Integer> (); 		lp39.add(2); lp39.add(0); lp39.add(1); 	eight.add(lp39);	
		ArrayList <Integer> lp40= new ArrayList <Integer> (); 		lp40.add(3); lp40.add(0); lp40.add(1);  eight.add(lp40); list.add(eight);
		
		ArrayList<ArrayList<Integer>> nine = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp41= new ArrayList <Integer> (); 		lp41.add(0); lp41.add(0); lp41.add(0); 	nine.add(lp41);
		ArrayList <Integer> lp42= new ArrayList <Integer> (); 		lp42.add(1); lp42.add(0); lp42.add(0); 	nine.add(lp42);	
		ArrayList <Integer> lp43= new ArrayList <Integer> (); 		lp43.add(1); lp43.add(1); lp43.add(0); 	nine.add(lp43);	
		ArrayList <Integer> lp44= new ArrayList <Integer> (); 		lp44.add(1); lp44.add(2); lp44.add(0); 	nine.add(lp44);	
		ArrayList <Integer> lp45= new ArrayList <Integer> (); 		lp45.add(1); lp45.add(3); lp45.add(0);  nine.add(lp45); list.add(nine);
		
		ArrayList<ArrayList<Integer>> ten = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp46= new ArrayList <Integer> (); 		lp46.add(3); lp46.add(0); lp46.add(0); 	ten.add(lp46);
		ArrayList <Integer> lp47= new ArrayList <Integer> (); 		lp47.add(0); lp47.add(1); lp47.add(0); 	ten.add(lp47);	
		ArrayList <Integer> lp48= new ArrayList <Integer> (); 		lp48.add(1); lp48.add(1); lp48.add(0); 	ten.add(lp48);	
		ArrayList <Integer> lp49= new ArrayList <Integer> (); 		lp49.add(2); lp49.add(1); lp49.add(0); 	ten.add(lp49);	
		ArrayList <Integer> lp50= new ArrayList <Integer> (); 		lp50.add(3); lp50.add(1); lp50.add(0);  ten.add(lp50); list.add(ten);
		
		ArrayList<ArrayList<Integer>> eleven = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp51= new ArrayList <Integer> (); 		lp51.add(0); lp51.add(0); lp51.add(0); 	eleven.add(lp51);
		ArrayList <Integer> lp52= new ArrayList <Integer> (); 		lp52.add(0); lp52.add(1); lp52.add(0); 	eleven.add(lp52);	
		ArrayList <Integer> lp53= new ArrayList <Integer> (); 		lp53.add(0); lp53.add(2); lp53.add(0); 	eleven.add(lp53);	
		ArrayList <Integer> lp54= new ArrayList <Integer> (); 		lp54.add(0); lp54.add(3); lp54.add(0); 	eleven.add(lp54);	
		ArrayList <Integer> lp55= new ArrayList <Integer> (); 		lp55.add(1); lp55.add(3); lp55.add(0);  eleven.add(lp55); list.add(eleven);
		
		ArrayList<ArrayList<Integer>> twelve = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp56= new ArrayList <Integer> (); 		lp56.add(0); lp56.add(0); lp56.add(0); 	twelve.add(lp56);
		ArrayList <Integer> lp57= new ArrayList <Integer> (); 		lp57.add(0); lp57.add(1); lp57.add(0); 	twelve.add(lp57);	
		ArrayList <Integer> lp58= new ArrayList <Integer> (); 		lp58.add(1); lp58.add(0); lp58.add(0); 	twelve.add(lp58);	
		ArrayList <Integer> lp59= new ArrayList <Integer> (); 		lp59.add(2); lp59.add(0); lp59.add(0); 	twelve.add(lp59);	
		ArrayList <Integer> lp60= new ArrayList <Integer> (); 		lp60.add(3); lp60.add(0); lp60.add(0);  twelve.add(lp60); list.add(twelve);
		
		ArrayList<ArrayList<Integer>> thirteen = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp61= new ArrayList <Integer> (); 		lp61.add(0); lp61.add(0); lp61.add(0); 	thirteen.add(lp61);
		ArrayList <Integer> lp62= new ArrayList <Integer> (); 		lp62.add(0); lp62.add(0); lp62.add(1); 	thirteen.add(lp62);	
		ArrayList <Integer> lp63= new ArrayList <Integer> (); 		lp63.add(0); lp63.add(1); lp63.add(0); 	thirteen.add(lp63);	
		ArrayList <Integer> lp64= new ArrayList <Integer> (); 		lp64.add(0); lp64.add(2); lp64.add(0); 	thirteen.add(lp64);	
		ArrayList <Integer> lp65= new ArrayList <Integer> (); 		lp65.add(0); lp65.add(3); lp65.add(0);  thirteen.add(lp65); list.add(thirteen);
		
		ArrayList<ArrayList<Integer>> fourteen = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp66= new ArrayList <Integer> (); 		lp66.add(0); lp66.add(0); lp66.add(0); 	fourteen.add(lp66);
		ArrayList <Integer> lp67= new ArrayList <Integer> (); 		lp67.add(1); lp67.add(0); lp67.add(0); 	fourteen.add(lp67);	
		ArrayList <Integer> lp68= new ArrayList <Integer> (); 		lp68.add(2); lp68.add(0); lp68.add(0); 	fourteen.add(lp68);	
		ArrayList <Integer> lp69= new ArrayList <Integer> (); 		lp69.add(3); lp69.add(0); lp69.add(0); 	fourteen.add(lp69);	
		ArrayList <Integer> lp70= new ArrayList <Integer> (); 		lp70.add(3); lp70.add(0); lp70.add(1);  fourteen.add(lp70); list.add(fourteen);
		
		ArrayList<ArrayList<Integer>> fifteen = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp71= new ArrayList <Integer> (); 		lp71.add(0); lp71.add(0); lp71.add(0); 	fifteen.add(lp71);
		ArrayList <Integer> lp72= new ArrayList <Integer> (); 		lp72.add(0); lp72.add(1); lp72.add(0); 	fifteen.add(lp72);	
		ArrayList <Integer> lp73= new ArrayList <Integer> (); 		lp73.add(0); lp73.add(2); lp73.add(0); 	fifteen.add(lp73);	
		ArrayList <Integer> lp74= new ArrayList <Integer> (); 		lp74.add(0); lp74.add(3); lp74.add(0); 	fifteen.add(lp74);	
		ArrayList <Integer> lp75= new ArrayList <Integer> (); 		lp75.add(0); lp75.add(3); lp75.add(1);  fifteen.add(lp75); list.add(fifteen);
		
		ArrayList<ArrayList<Integer>> sixteen = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> lp76= new ArrayList <Integer> (); 		lp76.add(0); lp76.add(0); lp76.add(0); 	sixteen.add(lp76);
		ArrayList <Integer> lp77= new ArrayList <Integer> (); 		lp77.add(0); lp77.add(0); lp77.add(1); 	sixteen.add(lp77);	
		ArrayList <Integer> lp78= new ArrayList <Integer> (); 		lp78.add(1); lp78.add(0); lp78.add(0); 	sixteen.add(lp78);	
		ArrayList <Integer> lp79= new ArrayList <Integer> (); 		lp79.add(2); lp79.add(0); lp79.add(0); 	sixteen.add(lp79);	
		ArrayList <Integer> lp80= new ArrayList <Integer> (); 		lp80.add(3); lp80.add(0); lp80.add(0);  sixteen.add(lp80); list.add(sixteen);
		
		return list;
	}
	
	public static ArrayList<ArrayList<ArrayList<Integer>>> getPFilled()
	{
		ArrayList<ArrayList<ArrayList<Integer>>> list = new ArrayList<ArrayList<ArrayList<Integer>>>();
		
		ArrayList<ArrayList<Integer>> one = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp1= new ArrayList <Integer> (); 		pp1.add(0); pp1.add(0); pp1.add(0); 	one.add(pp1);
		ArrayList <Integer> pp2= new ArrayList <Integer> (); 		pp2.add(0); pp2.add(1); pp2.add(0); 	one.add(pp2);	
		ArrayList <Integer> pp3= new ArrayList <Integer> (); 		pp3.add(0); pp3.add(2); pp3.add(0); 	one.add(pp3);	
		ArrayList <Integer> pp4= new ArrayList <Integer> (); 		pp4.add(1); pp4.add(1); pp4.add(0); 	one.add(pp4);
		ArrayList <Integer> pp5= new ArrayList <Integer> (); 		pp5.add(1); pp5.add(2); pp5.add(0); 	one.add(pp5); list.add(one);
		
		
		ArrayList<ArrayList<Integer>> two = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp6= new ArrayList <Integer> (); 		pp6.add(0); pp6.add(0); pp6.add(0); 	two.add(pp6);
		ArrayList <Integer> pp7= new ArrayList <Integer> (); 		pp7.add(1); pp7.add(0); pp7.add(0); 	two.add(pp7);
		ArrayList <Integer> pp8= new ArrayList <Integer> (); 		pp8.add(2); pp8.add(0); pp8.add(0); 	two.add(pp8);	
		ArrayList <Integer> pp9= new ArrayList <Integer> (); 		pp9.add(1); pp9.add(1); pp9.add(0); 	two.add(pp9);	
		ArrayList <Integer> pp10= new ArrayList <Integer> (); 		pp10.add(0); pp10.add(1); pp10.add(0);  two.add(pp10); list.add(two);
		
		
		ArrayList<ArrayList<Integer>> three = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp11= new ArrayList <Integer> (); 		pp11.add(0); pp11.add(0); pp11.add(0); 	three.add(pp11);
		ArrayList <Integer> pp12= new ArrayList <Integer> (); 		pp12.add(0); pp12.add(1); pp12.add(0); 	three.add(pp12);	
		ArrayList <Integer> pp13= new ArrayList <Integer> (); 		pp13.add(1); pp13.add(0); pp13.add(0); 	three.add(pp13);	
		ArrayList <Integer> pp14= new ArrayList <Integer> (); 		pp14.add(1); pp14.add(1); pp14.add(0); 	three.add(pp14);	
		ArrayList <Integer> pp15= new ArrayList <Integer> (); 		pp15.add(1); pp15.add(2); pp15.add(0);  three.add(pp15); list.add(three);
		
		ArrayList<ArrayList<Integer>> four = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp16= new ArrayList <Integer> (); 		pp16.add(0); pp16.add(1); pp16.add(0); 	four.add(pp16);
		ArrayList <Integer> pp17= new ArrayList <Integer> (); 		pp17.add(1); pp17.add(0); pp17.add(0); 	four.add(pp17);	
		ArrayList <Integer> pp18= new ArrayList <Integer> (); 		pp18.add(1); pp18.add(1); pp18.add(0); 	four.add(pp18);	
		ArrayList <Integer> pp19= new ArrayList <Integer> (); 		pp19.add(2); pp19.add(0); pp19.add(0); 	four.add(pp19);	
		ArrayList <Integer> pp20= new ArrayList <Integer> (); 		pp20.add(2); pp20.add(1); pp20.add(0);  four.add(pp20); list.add(four);
		
		ArrayList<ArrayList<Integer>> five = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp21= new ArrayList <Integer> (); 		pp21.add(0); pp21.add(0); pp21.add(1); 	five.add(pp21);
		ArrayList <Integer> pp22= new ArrayList <Integer> (); 		pp22.add(0); pp22.add(1); pp22.add(1); 	five.add(pp22);	
		ArrayList <Integer> pp23= new ArrayList <Integer> (); 		pp23.add(0); pp23.add(2); pp23.add(1); 	five.add(pp23);	
		ArrayList <Integer> pp24= new ArrayList <Integer> (); 		pp24.add(0); pp24.add(1); pp24.add(0); 	five.add(pp24);	
		ArrayList <Integer> pp25= new ArrayList <Integer> (); 		pp25.add(0); pp25.add(2); pp25.add(0);  five.add(pp25); list.add(five);
		
		ArrayList<ArrayList<Integer>> six = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp26= new ArrayList <Integer> (); 		pp26.add(0); pp26.add(0); pp26.add(0); 	six.add(pp26);
		ArrayList <Integer> pp27= new ArrayList <Integer> (); 		pp27.add(1); pp27.add(0); pp27.add(0); 	six.add(pp27);	
		ArrayList <Integer> pp28= new ArrayList <Integer> (); 		pp28.add(0); pp28.add(0); pp28.add(1); 	six.add(pp28);	
		ArrayList <Integer> pp29= new ArrayList <Integer> (); 		pp29.add(1); pp29.add(0); pp29.add(1); 	six.add(pp29);	
		ArrayList <Integer> pp30= new ArrayList <Integer> (); 		pp30.add(2); pp30.add(0); pp30.add(1);  six.add(pp30); list.add(six);
		
		ArrayList<ArrayList<Integer>> seven = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp31= new ArrayList <Integer> (); 		pp31.add(0); pp31.add(0); pp31.add(0); 	seven.add(pp31);
		ArrayList <Integer> pp32= new ArrayList <Integer> (); 		pp32.add(0); pp32.add(1); pp32.add(0); 	seven.add(pp32);	
		ArrayList <Integer> pp33= new ArrayList <Integer> (); 		pp33.add(0); pp33.add(0); pp33.add(1); 	seven.add(pp33);	
		ArrayList <Integer> pp34= new ArrayList <Integer> (); 		pp34.add(0); pp34.add(1); pp34.add(1); 	seven.add(pp34);	
		ArrayList <Integer> pp35= new ArrayList <Integer> (); 		pp35.add(0); pp35.add(2); pp35.add(1);  seven.add(pp35); list.add(seven);
		
		ArrayList<ArrayList<Integer>> eight = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp36= new ArrayList <Integer> (); 		pp36.add(0); pp36.add(0); pp36.add(1); 	eight.add(pp36);
		ArrayList <Integer> pp37= new ArrayList <Integer> (); 		pp37.add(1); pp37.add(0); pp37.add(1); 	eight.add(pp37);	
		ArrayList <Integer> pp38= new ArrayList <Integer> (); 		pp38.add(2); pp38.add(0); pp38.add(1); 	eight.add(pp38);	
		ArrayList <Integer> pp39= new ArrayList <Integer> (); 		pp39.add(1); pp39.add(0); pp39.add(0); 	eight.add(pp39);	
		ArrayList <Integer> pp40= new ArrayList <Integer> (); 		pp40.add(2); pp40.add(0); pp40.add(0);  eight.add(pp40); list.add(eight);
		
		ArrayList<ArrayList<Integer>> nine = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp41= new ArrayList <Integer> (); 		pp41.add(0); pp41.add(1); pp41.add(0); 	nine.add(pp41);
		ArrayList <Integer> pp42= new ArrayList <Integer> (); 		pp42.add(0); pp42.add(2); pp42.add(0); 	nine.add(pp42);	
		ArrayList <Integer> pp43= new ArrayList <Integer> (); 		pp43.add(1); pp43.add(0); pp43.add(0); 	nine.add(pp43);	
		ArrayList <Integer> pp44= new ArrayList <Integer> (); 		pp44.add(1); pp44.add(1); pp44.add(0); 	nine.add(pp44);	
		ArrayList <Integer> pp45= new ArrayList <Integer> (); 		pp45.add(1); pp45.add(2); pp45.add(0);  nine.add(pp45); list.add(nine);
		
		ArrayList<ArrayList<Integer>> ten = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp46= new ArrayList <Integer> (); 		pp46.add(0); pp46.add(0); pp46.add(0); 	ten.add(pp46);
		ArrayList <Integer> pp47= new ArrayList <Integer> (); 		pp47.add(1); pp47.add(0); pp47.add(0); 	ten.add(pp47);	
		ArrayList <Integer> pp48= new ArrayList <Integer> (); 		pp48.add(0); pp48.add(1); pp48.add(0); 	ten.add(pp48);	
		ArrayList <Integer> pp49= new ArrayList <Integer> (); 		pp49.add(1); pp49.add(1); pp49.add(0); 	ten.add(pp49);	
		ArrayList <Integer> pp50= new ArrayList <Integer> (); 		pp50.add(2); pp50.add(1); pp50.add(0);  ten.add(pp50); list.add(ten);
		
		ArrayList<ArrayList<Integer>> eleven = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp51= new ArrayList <Integer> (); 		pp51.add(0); pp51.add(0); pp51.add(0); 	eleven.add(pp51);
		ArrayList <Integer> pp52= new ArrayList <Integer> (); 		pp52.add(0); pp52.add(1); pp52.add(0); 	eleven.add(pp52);	
		ArrayList <Integer> pp53= new ArrayList <Integer> (); 		pp53.add(0); pp53.add(2); pp53.add(0); 	eleven.add(pp53);	
		ArrayList <Integer> pp54= new ArrayList <Integer> (); 		pp54.add(1); pp54.add(0); pp54.add(0); 	eleven.add(pp54);	
		ArrayList <Integer> pp55= new ArrayList <Integer> (); 		pp55.add(1); pp55.add(1); pp55.add(0);  eleven.add(pp55); list.add(eleven);
		
		ArrayList<ArrayList<Integer>> twelve = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp56= new ArrayList <Integer> (); 		pp56.add(0); pp56.add(0); pp56.add(0); 	twelve.add(pp56);
		ArrayList <Integer> pp57= new ArrayList <Integer> (); 		pp57.add(1); pp57.add(0); pp57.add(0); 	twelve.add(pp57);	
		ArrayList <Integer> pp58= new ArrayList <Integer> (); 		pp58.add(2); pp58.add(0); pp58.add(0); 	twelve.add(pp58);	
		ArrayList <Integer> pp59= new ArrayList <Integer> (); 		pp59.add(1); pp59.add(1); pp59.add(0); 	twelve.add(pp59);	
		ArrayList <Integer> pp60= new ArrayList <Integer> (); 		pp60.add(2); pp60.add(1); pp60.add(0);  twelve.add(pp60); list.add(twelve);
		
		ArrayList<ArrayList<Integer>> thirteen = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp61= new ArrayList <Integer> (); 		pp61.add(0); pp61.add(0); pp61.add(0); 	thirteen.add(pp61);
		ArrayList <Integer> pp62= new ArrayList <Integer> (); 		pp62.add(0); pp62.add(1); pp62.add(0); 	thirteen.add(pp62);	
		ArrayList <Integer> pp63= new ArrayList <Integer> (); 		pp63.add(0); pp63.add(2); pp63.add(0); 	thirteen.add(pp63);	
		ArrayList <Integer> pp64= new ArrayList <Integer> (); 		pp64.add(0); pp64.add(1); pp64.add(1); 	thirteen.add(pp64);	
		ArrayList <Integer> pp65= new ArrayList <Integer> (); 		pp65.add(0); pp65.add(2); pp65.add(1);  thirteen.add(pp65); list.add(thirteen);
		
		ArrayList<ArrayList<Integer>> fourteen = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp66= new ArrayList <Integer> (); 		pp66.add(0); pp66.add(0); pp66.add(0); 	fourteen.add(pp66);
		ArrayList <Integer> pp67= new ArrayList <Integer> (); 		pp67.add(1); pp67.add(0); pp67.add(0); 	fourteen.add(pp67);	
		ArrayList <Integer> pp68= new ArrayList <Integer> (); 		pp68.add(2); pp68.add(0); pp68.add(0); 	fourteen.add(pp68);	
		ArrayList <Integer> pp69= new ArrayList <Integer> (); 		pp69.add(0); pp69.add(0); pp69.add(1); 	fourteen.add(pp69);	
		ArrayList <Integer> pp70= new ArrayList <Integer> (); 		pp70.add(1); pp70.add(0); pp70.add(1);  fourteen.add(pp70); list.add(fourteen);
		
		ArrayList<ArrayList<Integer>> fifteen = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp71= new ArrayList <Integer> (); 		pp71.add(0); pp71.add(0); pp71.add(0); 	fifteen.add(pp71);
		ArrayList <Integer> pp72= new ArrayList <Integer> (); 		pp72.add(0); pp72.add(1); pp72.add(0); 	fifteen.add(pp72);	
		ArrayList <Integer> pp73= new ArrayList <Integer> (); 		pp73.add(0); pp73.add(2); pp73.add(0); 	fifteen.add(pp73);	
		ArrayList <Integer> pp74= new ArrayList <Integer> (); 		pp74.add(0); pp74.add(0); pp74.add(1); 	fifteen.add(pp74);	
		ArrayList <Integer> pp75= new ArrayList <Integer> (); 		pp75.add(0); pp75.add(1); pp75.add(1);  fifteen.add(pp75); list.add(fifteen);
		
		ArrayList<ArrayList<Integer>> sixteen = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> pp76= new ArrayList <Integer> (); 		pp76.add(0); pp76.add(0); pp76.add(0); 	sixteen.add(pp76);
		ArrayList <Integer> pp77= new ArrayList <Integer> (); 		pp77.add(1); pp77.add(0); pp77.add(0); 	sixteen.add(pp77);	
		ArrayList <Integer> pp78= new ArrayList <Integer> (); 		pp78.add(2); pp78.add(0); pp78.add(0); 	sixteen.add(pp78);	
		ArrayList <Integer> pp79= new ArrayList <Integer> (); 		pp79.add(1); pp79.add(0); pp79.add(1); 	sixteen.add(pp79);	
		ArrayList <Integer> pp80= new ArrayList <Integer> (); 		pp80.add(2); pp80.add(0); pp80.add(1);  sixteen.add(pp80); list.add(sixteen);
		
		
		return list;
	}
	
	public static ArrayList<ArrayList<ArrayList<Integer>>> getTFilled()
	{
		ArrayList<ArrayList<ArrayList<Integer>>> list = new ArrayList<ArrayList<ArrayList<Integer>>>();
		
		ArrayList<ArrayList<Integer>> one = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp1= new ArrayList <Integer> (); 		tp1.add(1); tp1.add(0); tp1.add(0); 	one.add(tp1);
		ArrayList <Integer> tp2= new ArrayList <Integer> (); 		tp2.add(1); tp2.add(1); tp2.add(0); 	one.add(tp2);	
		ArrayList <Integer> tp3= new ArrayList <Integer> (); 		tp3.add(1); tp3.add(2); tp3.add(0); 	one.add(tp3);	
		ArrayList <Integer> tp4= new ArrayList <Integer> (); 		tp4.add(0); tp4.add(2); tp4.add(0); 	one.add(tp4);
		ArrayList <Integer> tp5= new ArrayList <Integer> (); 		tp5.add(2); tp5.add(2); tp5.add(0); 	one.add(tp5); list.add(one);
		
		
		ArrayList<ArrayList<Integer>> two = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp6= new ArrayList <Integer> (); 		tp6.add(0); tp6.add(0); tp6.add(0); 	two.add(tp6);
		ArrayList <Integer> tp7= new ArrayList <Integer> (); 		tp7.add(0); tp7.add(1); tp7.add(0); 	two.add(tp7);
		ArrayList <Integer> tp8= new ArrayList <Integer> (); 		tp8.add(0); tp8.add(2); tp8.add(0); 	two.add(tp8);	
		ArrayList <Integer> tp9= new ArrayList <Integer> (); 		tp9.add(1); tp9.add(1); tp9.add(0); 	two.add(tp9);	
		ArrayList <Integer> tp10= new ArrayList <Integer> (); 		tp10.add(2); tp10.add(1); tp10.add(0);  two.add(tp10); list.add(two);
		
		
		ArrayList<ArrayList<Integer>> three = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp11= new ArrayList <Integer> (); 		tp11.add(0); tp11.add(0); tp11.add(0); 	three.add(tp11);
		ArrayList <Integer> tp12= new ArrayList <Integer> (); 		tp12.add(1); tp12.add(0); tp12.add(0); 	three.add(tp12);	
		ArrayList <Integer> tp13= new ArrayList <Integer> (); 		tp13.add(2); tp13.add(0); tp13.add(0); 	three.add(tp13);	
		ArrayList <Integer> tp14= new ArrayList <Integer> (); 		tp14.add(1); tp14.add(1); tp14.add(0); 	three.add(tp14);	
		ArrayList <Integer> tp15= new ArrayList <Integer> (); 		tp15.add(1); tp15.add(2); tp15.add(0);  three.add(tp15); list.add(three);
		
		ArrayList<ArrayList<Integer>> four = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp16= new ArrayList <Integer> (); 		tp16.add(0); tp16.add(1); tp16.add(0); 	four.add(tp16);
		ArrayList <Integer> tp17= new ArrayList <Integer> (); 		tp17.add(1); tp17.add(1); tp17.add(0); 	four.add(tp17);	
		ArrayList <Integer> tp18= new ArrayList <Integer> (); 		tp18.add(2); tp18.add(1); tp18.add(0); 	four.add(tp18);	
		ArrayList <Integer> tp19= new ArrayList <Integer> (); 		tp19.add(2); tp19.add(0); tp19.add(0); 	four.add(tp19);	
		ArrayList <Integer> tp20= new ArrayList <Integer> (); 		tp20.add(2); tp20.add(2); tp20.add(0);  four.add(tp20); list.add(four);
		
		ArrayList<ArrayList<Integer>> five = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp21= new ArrayList <Integer> (); 		tp21.add(0); tp21.add(0); tp21.add(1); 	five.add(tp21);
		ArrayList <Integer> tp22= new ArrayList <Integer> (); 		tp22.add(0); tp22.add(1); tp22.add(1); 	five.add(tp22);	
		ArrayList <Integer> tp23= new ArrayList <Integer> (); 		tp23.add(0); tp23.add(2); tp23.add(1); 	five.add(tp23);	
		ArrayList <Integer> tp24= new ArrayList <Integer> (); 		tp24.add(0); tp24.add(2); tp24.add(0); 	five.add(tp24);	
		ArrayList <Integer> tp25= new ArrayList <Integer> (); 		tp25.add(0); tp25.add(2); tp25.add(2);  five.add(tp25); list.add(five);
		
		ArrayList<ArrayList<Integer>> six = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp26= new ArrayList <Integer> (); 		tp26.add(0); tp26.add(0); tp26.add(0); 	six.add(tp26);
		ArrayList <Integer> tp27= new ArrayList <Integer> (); 		tp27.add(0); tp27.add(0); tp27.add(1); 	six.add(tp27);	
		ArrayList <Integer> tp28= new ArrayList <Integer> (); 		tp28.add(0); tp28.add(0); tp28.add(2); 	six.add(tp28);	
		ArrayList <Integer> tp29= new ArrayList <Integer> (); 		tp29.add(1); tp29.add(0); tp29.add(1); 	six.add(tp29);	
		ArrayList <Integer> tp30= new ArrayList <Integer> (); 		tp30.add(2); tp30.add(0); tp30.add(1);  six.add(tp30); list.add(six);
		
		ArrayList<ArrayList<Integer>> seven = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp31= new ArrayList <Integer> (); 		tp31.add(0); tp31.add(0); tp31.add(0); 	seven.add(tp31);
		ArrayList <Integer> tp32= new ArrayList <Integer> (); 		tp32.add(0); tp32.add(0); tp32.add(1); 	seven.add(tp32);	
		ArrayList <Integer> tp33= new ArrayList <Integer> (); 		tp33.add(0); tp33.add(0); tp33.add(2); 	seven.add(tp33);	
		ArrayList <Integer> tp34= new ArrayList <Integer> (); 		tp34.add(0); tp34.add(1); tp34.add(1); 	seven.add(tp34);	
		ArrayList <Integer> tp35= new ArrayList <Integer> (); 		tp35.add(0); tp35.add(2); tp35.add(1);  seven.add(tp35); list.add(seven);
		
		ArrayList<ArrayList<Integer>> eight = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp36= new ArrayList <Integer> (); 		tp36.add(0); tp36.add(0); tp36.add(1); 	eight.add(tp36);
		ArrayList <Integer> tp37= new ArrayList <Integer> (); 		tp37.add(1); tp37.add(0); tp37.add(1); 	eight.add(tp37);	
		ArrayList <Integer> tp38= new ArrayList <Integer> (); 		tp38.add(2); tp38.add(0); tp38.add(1); 	eight.add(tp38);	
		ArrayList <Integer> tp39= new ArrayList <Integer> (); 		tp39.add(2); tp39.add(0); tp39.add(0); 	eight.add(tp39);	
		ArrayList <Integer> tp40= new ArrayList <Integer> (); 		tp40.add(2); tp40.add(0); tp40.add(2);  eight.add(tp40); list.add(eight);
		
		ArrayList<ArrayList<Integer>> nine = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp41= new ArrayList <Integer> (); 		tp41.add(0); tp41.add(1); tp41.add(0); 	nine.add(tp41);
		ArrayList <Integer> tp42= new ArrayList <Integer> (); 		tp42.add(0); tp42.add(1); tp42.add(1); 	nine.add(tp42);	
		ArrayList <Integer> tp43= new ArrayList <Integer> (); 		tp43.add(0); tp43.add(1); tp43.add(2); 	nine.add(tp43);	
		ArrayList <Integer> tp44= new ArrayList <Integer> (); 		tp44.add(0); tp44.add(0); tp44.add(2); 	nine.add(tp44);	
		ArrayList <Integer> tp45= new ArrayList <Integer> (); 		tp45.add(0); tp45.add(2); tp45.add(2);  nine.add(tp45); list.add(nine);
		
		ArrayList<ArrayList<Integer>> ten = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp46= new ArrayList <Integer> (); 		tp46.add(0); tp46.add(0); tp46.add(2); 	ten.add(tp46);
		ArrayList <Integer> tp47= new ArrayList <Integer> (); 		tp47.add(1); tp47.add(0); tp47.add(0); 	ten.add(tp47);	
		ArrayList <Integer> tp48= new ArrayList <Integer> (); 		tp48.add(1); tp48.add(0); tp48.add(1); 	ten.add(tp48);	
		ArrayList <Integer> tp49= new ArrayList <Integer> (); 		tp49.add(1); tp49.add(0); tp49.add(2); 	ten.add(tp49);	
		ArrayList <Integer> tp50= new ArrayList <Integer> (); 		tp50.add(2); tp50.add(0); tp50.add(2);  ten.add(tp50); list.add(ten);
		
		ArrayList<ArrayList<Integer>> eleven = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp51= new ArrayList <Integer> (); 		tp51.add(0); tp51.add(0); tp51.add(0); 	eleven.add(tp51);
		ArrayList <Integer> tp52= new ArrayList <Integer> (); 		tp52.add(0); tp52.add(1); tp52.add(0); 	eleven.add(tp52);	
		ArrayList <Integer> tp53= new ArrayList <Integer> (); 		tp53.add(0); tp53.add(2); tp53.add(0); 	eleven.add(tp53);	
		ArrayList <Integer> tp54= new ArrayList <Integer> (); 		tp54.add(0); tp54.add(1); tp54.add(1); 	eleven.add(tp54);	
		ArrayList <Integer> tp55= new ArrayList <Integer> (); 		tp55.add(0); tp55.add(1); tp55.add(2);  eleven.add(tp55); list.add(eleven);
		
		ArrayList<ArrayList<Integer>> twelve = new ArrayList<ArrayList<Integer>>();
		ArrayList <Integer> tp56= new ArrayList <Integer> (); 		tp56.add(0); tp56.add(0); tp56.add(0); 	twelve.add(tp56);
		ArrayList <Integer> tp57= new ArrayList <Integer> (); 		tp57.add(1); tp57.add(0); tp57.add(0); 	twelve.add(tp57);	
		ArrayList <Integer> tp58= new ArrayList <Integer> (); 		tp58.add(2); tp58.add(0); tp58.add(0); 	twelve.add(tp58);	
		ArrayList <Integer> tp59= new ArrayList <Integer> (); 		tp59.add(1); tp59.add(0); tp59.add(2); 	twelve.add(tp59);	
		ArrayList <Integer> tp60= new ArrayList <Integer> (); 		tp60.add(1); tp60.add(0); tp60.add(1);  twelve.add(tp60); list.add(twelve);
		return list;
	}
	
}
