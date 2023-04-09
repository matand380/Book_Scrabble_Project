package Model.GameLogic;

public interface CacheReplacementPolicy{
	void add(String word);
	String remove(); 
}
