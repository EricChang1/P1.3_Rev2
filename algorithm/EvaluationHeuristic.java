package algorithm;
import models.*;
import models.Resource.BlockType;
public interface EvaluationHeuristic {
	
	public double getScore(Resource block, Position pos, int state);
	public void update(Resource res, Position pos, BlockType type, int state);
	public Position freePos(Container truck);
	public boolean checkPos(Position pos, Block block, Container container);
	public void nextPos();
	public boolean pentCheckPos(Resource res, int state, Position pos, Container container);


}
