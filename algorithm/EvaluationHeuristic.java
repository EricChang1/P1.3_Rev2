package algorithm;

import models.*;
public interface EvaluationHeuristic {
	
	public double getScore(Block block, Position pos);
	

}
