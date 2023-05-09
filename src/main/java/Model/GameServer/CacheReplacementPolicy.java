package Model.GameServer;

public interface CacheReplacementPolicy{
	void add(String word);
	String remove(); 
}
