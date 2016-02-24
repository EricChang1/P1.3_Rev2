package algorithm;

public class EvaluationHeuristicFactory {
	
	public EvaluationHeuristic getHeuristicEval(EvaluationHeuristicType h)
	{
		switch(h)
		{
			case MaxDensity: return new MaximumDensity();
			
		}
		
		return null;
	}

}
