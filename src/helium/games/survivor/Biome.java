package helium.games.survivor;

public class Biome {

	public boolean hasWater = true;
	public String surfaceBlock = Level.GRASS;
	public String underBlock = Level.DIRT;
	public String treeType = Level.TREE_DEFAULT;
	public int treeDensity = 6;	//Minimum block space between each tree
	public int treeChance = 6;	
	public int stoneChance = 10;	
	public int silverChance = 30;
	public int goldChance = 50;	
	public int flatness = 2;	//Random int, 0 - x. if random int != x stay flat. Bigger the # = flatter
	public int waterChance = 4; //Bigger the # = less chance of water
	public int waterLength= 1; //Bigger the # = smaller pond
	
	public int birdChance = 6;
	public int rabbitChance = 40;
	public int fishChance = 2;
	
	
	public Biome( String type ) {

		setBiome(type);

	}

	public void setBiome(String type){

		if(type == Level.SNOW){
			
			this.hasWater = false;
			this.surfaceBlock = Level.SNOW;
			this.underBlock = Level.DIRT;
			this.treeChance = 3;
			this.treeType = Level.TREE_SNOW;
			this.treeDensity = 4;
			this.flatness = 2; 
			this.waterChance = 6; 
			this.waterLength= 1;
			
		}
		
		if(type == Level.GRASS){
			
			this.hasWater = true;
			this.surfaceBlock = Level.GRASS;
			this.underBlock = Level.DIRT;
			this.treeChance = 2;
			this.treeType = Level.TREE_DEFAULT;
			this.treeDensity = 6;
			this.flatness = 2; 
			this.waterChance = 4; 
			this.waterLength= 1;
			
		}

	}
}
