package helium.games.survivor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Level extends Activity{

	public static final int BUTTON_SIZE = 100;
	public static final String TYPE_NEW = "TYPE_NEW";
	public static final String TYPE_CONT = "TYPE_CONT";
	public static final String GRASS= "Grass";
	public static final String SNOW = "Snow";
	public static final String DIRT = "Dirt";
	public static final String SAND = "Sand";
	public static final String STONE = "STONE";
	public static final String SILVER = "SILVER";
	public static final String GOLD = "GOLD";
	public static final String WATER = "Water";
	public static final String TREE_DEFAULT = "Default Tree";
	public static final String TREE_SNOW = "Snow Tree";
	public static final String WORLD = "World";
	public static final String INVENTORY = "Inventory";
	public static final String JOURNAL = "Journal";
	public static final String CABIN = "Cabin";
	public static final String LOADING = "Loading";
	public static final String PAUSE = "Pause";
	public static final String QUIT = "Quit";
	public static final int    MAXBLOCKHEIGHT = 5;
	public static final int    SNOW_ADJ = 5;
	public static final String RIGHT = "Right";
	public static final String LEFT = "Left";
	public static final String UP = "Up";
	public static final String DOWN = "Down";
	public static final String NO = "No";
	public static final String ATTACK = "ATTACK";
	public static final String EQUIP = "EQUIP";
	public static final String TOSS = "TOSS";
	public static final String INVENTORY_ITEM = "Inventory item";
	public static final String CRAFTING_TABLE = "Crafting table";


	private static final int BLOCKS_HOME = 20; //Blocks on screen;
	public boolean DEV_MODE = false;

	private String getType = "NULL";

	public static int screen_width = 1;
	public static int screen_height = 1;
	public int playerEdgeWidth = 100;

	public  int cursorCenter =1;
	public  int cursorX =1;
	public  int cursorY =1;
	public  int cursorSizeP =25;
	public  int cursorSize =100;
	public  int cursorAlpha =55;
	public Bitmap cursorBG;
	public  float cursorUserX =1;
	public  float cursorUserY =1;
	public  boolean cursorDown = false;
	public  float cursorDownX = 1;
	public  float cursorDownY = 1;
	public int movePlayerVal = 0;
	public boolean moveWait = false;
	public int lastJumpY = 0;
	SharedPreferences getSettings;
	SharedPreferences getPlayerSettings;
	SharedPreferences.Editor prefEditor;
	SharedPreferences.Editor playEditor;

	private String player_name = "Jack Survivor";
	public int player_sizep = 10;


	private float world_time =  12.00001f;
	public int gravityRefresh = 5;
	public String home_biome = "Snow";
	public String screen = "World";
	public Bitmap world_bg;
	public Bitmap stars_bg;
	public Bitmap cabin_bg;
	public Bitmap inventory_bg;
	public Bitmap journal_bg;
	public Bitmap pause_bg;

	public int height_bottom=0;
	public int block_size = 50;
	public int nextBlockRightX = 0;
	public int nextBlockLeftX = 0;
	public int blocksRightCount = 0;
	public int blocksLeftCount = 1;
	public int lastBlockHeight = 0;
	public int nextBlockHeight = 0;		//0 = do nothing, 1 = same as last height.
	public String lastBlock = DIRT;
	public boolean lastBlockHasObject = false;
	public InventoryItem selectedItem;
	public InventoryItem equippedItem;

	/*private InventoryItem inventory;
	private Blocks block;
	private NPC npcs;
	 */

	public int cameraX = 0;

	public List<Blocks> block;
	public List<NPC> npc;
	public List<Tree> tree;
	public List<Stone> stone;
	public List<ButtonArea> buttonAreas;
	public List<InventoryItem> screenInventory;			//If player tosses something it will draw in the world view. If its in cabin area, it will save.
	public List<ScreenObject> screenEffects;
	public ArrayList<String> toastLog = new ArrayList<String>();
	public Effects effects;
	public Player player;
	public Biome biome = new Biome(GRASS);
	Canvas canvas;
	Bitmap canvasB;
	Handler handler = new Handler();
	Handler playerHandler = new Handler();
	Handler timeHandler = new Handler();
	public int timeDelay = 100;
	public int holdingBlock = 0;
	public boolean holding = false;

	LevelView levelView = null;
	public boolean canDraw=true;

	@Override
	public void onBackPressed() {
		//Intent i = new Intent(getParent().getIntent());
		//startActivity(i);

		if(screen == WORLD){
			openPause();
			//finish();
			return;
		}
		if(screen != WORLD){
			openWorld();
			return;
		}
		super.onBackPressed();	
	}

	@Override
	protected void onResume() {

		super.onResume();
		levelView.LevelView_OnResume();

	}

	@Override
	protected void onPause() {

		super.onPause();
		openPause();
		levelView.LevelView_OnPause();

	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		DisplayMetrics dismet = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dismet);
		block_size= dismet.widthPixels/BLOCKS_HOME;
		height_bottom = dismet.heightPixels - (block_size) ;
		screen_width = dismet.widthPixels;
		screen_height = dismet.heightPixels;
		cursorSize = dismet.widthPixels/(100/cursorSizeP);
		cursorY = (dismet.heightPixels - cursorSize)-10;
		cursorX = 10;
		cursorBG = BitmapFactory.decodeResource(getResources(), R.drawable.movebg);
		player = new Player(player_name, getResources(), this);
		player.spawn((screen_width/2), (screen_height/2), dismet.widthPixels/player_sizep);
		player.jumpheight = block_size*2;
		world_bg = BitmapFactory.decodeResource(getResources(), R.drawable.backgroundnew);
		stars_bg = BitmapFactory.decodeResource(getResources(), R.drawable.stars);
		journal_bg = BitmapFactory.decodeResource(getResources(), R.drawable.journalbg);
		inventory_bg = BitmapFactory.decodeResource(getResources(), R.drawable.inventory);
		pause_bg = BitmapFactory.decodeResource(getResources(), R.drawable.pause);

		if(world_bg != null){
			levelView = new LevelView(this,screen_width,screen_height, this);
			levelView.canvasW = dismet.widthPixels;
			levelView.canvasH = dismet.heightPixels;   
			Draw(LOADING);
			setContentView(levelView);
		}else{
			toast("ERROR - couldnt generate world",5);
		}
		player.inventory = new ArrayList<InventoryItem>();
		screenInventory = new ArrayList<InventoryItem>();
		block = new ArrayList<Blocks>();
		buttonAreas = new ArrayList<ButtonArea>();
		npc = new ArrayList<NPC>();
		tree = new ArrayList<Tree>();
		stone = new ArrayList<Stone>();
		screenEffects = new ArrayList<ScreenObject>();

		getSettings = getSharedPreferences("getSettings",0); 
		getPlayerSettings = getSharedPreferences("getPlayerSettings",0); 
		prefEditor = getSettings.edit(); 
		playEditor = getPlayerSettings.edit();
		Intent geti = getIntent();

		getType = geti.getStringExtra("Type");
		if(getType.equals(TYPE_NEW)){

			loadNewGame();

		}
		else if(getType.equals(TYPE_CONT)){

			loadContGame();

		}
		else{

			toast("Error loading game",5);

		}

		setupEffects();

	}

	public void setupEffects(){

		effects = new Effects( getResources(),this );

	}

	public void loadNewGame(){
		//Create world settings
		Random random = new Random();
		int randint = random.nextInt(2);
		if(randint==0){
			home_biome = SNOW;
			biome.setBiome(SNOW);
		}
		else if(randint==1){
			home_biome = GRASS;
			biome.setBiome(GRASS);
		}
		prefEditor.putString("Home", home_biome);
		prefEditor.putFloat("Time", 12.00001f);
		prefEditor.putString("Cabin","{}");

		prefEditor.commit();

		//Create player settings
		playEditor.putString("Name", player_name);
		playEditor.putInt("Health",100);
		playEditor.putInt("Hunger",100);	
		playEditor.putInt("Thirst",100);
		playEditor.putInt("Survived",0);
		playEditor.putString("Inventory","{}");
		playEditor.commit();

		//TODO clear inventory,cabin,moon, etc.
		player.inventory.add( new InventoryItem( player.weapon, getResources(), this ) );
		player.weapon = new Weapon( getResources(), this, Weapon.TORCH );

		player.inventory.add( new InventoryItem( player.weapon, getResources(), this ) );
		selectedItem = player.inventory.get(0);
		equippedItem = selectedItem;
		player.weapon = new Weapon( getResources(), this, Weapon.ROCK );

		player.inventory.add( new InventoryItem( player.weapon, getResources(), this ) );
		player.inventory.get(player.inventory.size() -1).halfCol = true;
		player.inventory.add( new InventoryItem( player.weapon, getResources(), this ) );
		player.inventory.get(player.inventory.size() -1).halfCol = true;
		player.inventory.add( new InventoryItem( player.weapon, getResources(), this ) );
		player.inventory.get(player.inventory.size() -1).halfCol = true;
		
		player.weapon = new Weapon( getResources(), this, Weapon.AXE );

		player.inventory.add( new InventoryItem(Blocks.WOOD_WALL, Blocks.WOOD_WALL, INVENTORY_ITEM, 50, BitmapFactory.decodeResource(getResources(), R.drawable.woodwall), false , getResources(), this ) );
		loadContGame();
	}

	public void loadContGame(){
		if(ReadData()==true){
			loadHome();
		}

	}

	public boolean  ReadData(){

		//Read world settings
		home_biome = getSettings.getString("Home", GRASS);
		world_time = getSettings.getFloat("Time", 12.00001f);
		String tmpX = getSettings.getString("Moon", "");

		if(tmpX.length() > 2){

			tmpX = tmpX.substring(0, tmpX.indexOf(","));
			int sunX = Integer.parseInt(tmpX);
			levelView.sun.x = sunX;
			String tmpY = getSettings.getString("Moon", "");
			tmpY =  tmpY.substring(tmpY.indexOf(",")+1);
			int sunY = Integer.parseInt(tmpY);
			levelView.sun.y = sunY;
		}


		//Read player settings
		player.health = getPlayerSettings.getInt("Health", 100);
		player.hunger = getPlayerSettings.getInt("Hunger", 100);
		player.thirst = getPlayerSettings.getInt("Thirst", 100);
		player.survived = getPlayerSettings.getInt("Survived", 0);


		String invItems = getPlayerSettings.getString("Inventory", "{}");
		String item = "";
		int startOfItem = 0;
		int endOfItem = 0;

		while(invItems.length() > 2){
			startOfItem = invItems.indexOf("[") + 1;
			endOfItem = invItems.indexOf("]");
			try{
				item = invItems.substring(startOfItem, endOfItem);
				String tmp = "[" + item + "]";


				invItems = invItems.replace(tmp, "");

				String name = item.substring(0, item.indexOf(","));
				item = item.replace(name + ",", "");

				String displayName = item.substring(0, item.indexOf(","));
				item = item.replace(displayName + ",", "");

				String type = item.substring(0, item.indexOf(","));
				item = item.replace(type + ",", "");
				int amount = Integer.parseInt(item);

				player.inventory.add( new InventoryItem( name, displayName, type, amount, pause_bg, true, getResources(), this ));
			}catch( StringIndexOutOfBoundsException e ){
				invItems = "{}";
			}
		}

		String cinvItems = getSettings.getString("Cabin", "{}");
		String item1 = "";
		int startOfItem1 = 0;
		int endOfItem1 = 0;

		while(cinvItems.length() > 2){
			startOfItem1 = cinvItems.indexOf("[") + 1;
			endOfItem1 = cinvItems.indexOf("]");
			try{
				item1 = cinvItems.substring(startOfItem1, endOfItem1);
				String tmp = "[" + item1 + "]";

				cinvItems = cinvItems.replace(tmp, "");

				String name = item1.substring(0, item1.indexOf(","));
				item1 = item1.replace(name + ",", "");

				String type = item1.substring(0, item1.indexOf(","));
				item1 = item1.replace(type + ",", "");

				String xS = item1.substring(0, item1.indexOf(","));
				item1 = item1.replace(xS + ",", "");

				String yS = item1.substring(0, item1.indexOf(","));
				item1 = item1.replace(yS + ",", "");

				String sizeS = item1.substring(0, item1.indexOf(","));
				item1 = item1.replace(sizeS + ",", "");

				String solidS = item1.substring(0, item1.indexOf(","));
				item1 = item1.replace(solidS + ",", "");
				Blocks blck =  new Blocks( INVENTORY_ITEM, block.size(), 0, Integer.parseInt(sizeS), Integer.parseInt(xS), Integer.parseInt(yS), Boolean.parseBoolean(solidS), getResources(), this );
				blck.name = name;

				Bitmap cImage = Bitmap.createScaledBitmap(retrieveItemImage(name), Integer.parseInt(sizeS), Integer.parseInt(sizeS), true);
				blck.animation = new Animation(cImage, blck.getX(), blck.getY(), blck.getSize(), blck.getSize());
				block.add(blck);


			}catch( StringIndexOutOfBoundsException e ){
				cinvItems = "{}";
			}
		}

		return true;

	}

	public void loadHome(){

		nextBlockLeftX = -(block_size);
		for (int i = 0; i < BLOCKS_HOME+1; i++) {
			block.add(new Blocks(home_biome,i,0,block_size,(i*block_size),height_bottom,true, getResources(),this));
			nextBlockRightX = (i+1)*block_size;
			blocksRightCount++;
		}

		handler.post(gravity); // start gravity
		handler.post(checkCol); // start colision checking
		timeHandler.post(timer);

		openWorld();
	}

	private Runnable gravity = new Runnable() {  

		public void run() {  

			if(!player.isOnGround && (player.getY() + player.size)<screen_height){

				player.moveY(1);

			}
			if((player.getY() + player.size) >= screen_height){
				player.isOnGround = true;
			}
			for ( NPC npcs : npc){
				if(!npcs.isOnGround){
					if(!npcs.gravityResistant){
						if(!npcs.flying){
							npcs.moveY(1);
						}
						//Check if is on ground
					}
				}
				if((npcs.getY() + npcs.height) >= screen_height){
					npcs.isOnGround = true;
				}
			}


			for ( InventoryItem iItem : player.inventory){
				if(!iItem.isOnGround){
					if(iItem.isInInventory == false){

						iItem.moveY(1);
						//Check if is on ground
					}

				}
				if((iItem.getY() + iItem.height) >= screen_height){
					iItem.isOnGround = true;
				}
			}



			//if(player.isOnGround==false){
			handler.postDelayed(gravity, gravityRefresh);
			//}

		}

	};

	private Runnable timer = new Runnable() {  

		public void run() {

			if(toastLog.size() > 0){
				toastLog.remove(0);
			}

			if(world_time >= 23){
				world_time = 0.00001f;
			}

			levelView.sun.x += 1;


			if(world_time > 6 && world_time < 18 ){

				levelView.sun.x = 0 - levelView.glow.getWidth() + (levelView.glow.getWidth()/3);
				levelView.sun.y = screen_height/5;
				player.isCold = false;
			}else{

				player.isCold = true;

				if(levelView.sun.x < screen_width/2 + (levelView.glow.getWidth()/2)){
					if( levelView.sun.y > 0 - (levelView.glow.getHeight()/2)  ){

						levelView.sun.y -= 1;



					}
				}else{
					if( levelView.sun.y < screen_height/10){

						levelView.sun.y += 1;



					}
				}
			}

			world_time+= .01f;

			if(player.weapon.type.equals(Weapon.WEAPON) && player.weapon.name.equals(Weapon.TORCH)){
				player.weapon.drainHealth(1);
				equippedItem.health = player.weapon.health;
				player.isCold = false;
			}


			player.updateStatus();



			timeHandler.postDelayed(timer, timeDelay);
		}

	};

	Blocks distancetest;
	int distancetext = 0;

	private Runnable checkCol = new Runnable() {  

		public void run() {  
			blstring = "";
			List<Blocks> blocksarea = findBlocksInArea(player.bodyCollision());
			distancetest = block.get(15);
			distancetext = getDistanceX(player.bodyCollision(), distancetest.Collision());
			int solidblocks = 0;
			for(Blocks bls : blocksarea){

				Rect Col =  bls.Collision();
				//Left				Top		Right			Bottom
				Col = new Rect(Col.left -cameraX,Col.top,Col.right -cameraX,Col.bottom);
				if(Rect.intersects(Col, player.feetCollision())){

					blstring+= bls.getType() + " , " ;

					if( bls.isSolid == true ){
						solidblocks++;

					}



				}
				checkHorizontalCol(bls,solidblocks, blocksarea.size());

			}
			

			if(blocksarea.size()>0){
				if(solidblocks>0){

					player.isOnGround=true;

				}else{
					player.isOnGround = false;
					if((player.getY() + player.size) >= screen_height){
						player.isOnGround = true;
					}
				}

			}
			//if(player.isOnGround==false){
			handler.post(checkCol);
			//}
		}     

	};

	public void checkHorizontalCol(Blocks bls,int currentint, int intcount){	//follows same idea for solidblock count

		String object1Direction = LEFT;

		if(getDistanceX(player.bodyCollision(),bls.Collision()) ==0){

			if(bls.getY()+(bls.getSize()/2) < player.feetCollision().bottom){
				if(bls.isSolid==true){
					player.lastTouchedBlock = bls;
					if( player.bodyCollision().centerX() > bls.Collision().centerX()-cameraX ){
						if( player.bodyCollision().top < bls.Collision().bottom ){
							if(player.movingDir == LEFT){
								object1Direction = RIGHT;
								player.canMoveLeft = false;
							}
						}
					}else{
						if( player.bodyCollision().top < bls.Collision().bottom ){

							if(player.movingDir==RIGHT){
								object1Direction = LEFT;
								player.canMoveRight = false;
							}
						}
					}
				}
			}
		}else{
			if(player.lastTouchedBlock != null){
				if(getDistanceX(player.bodyCollision(),player.lastTouchedBlock.Collision()) > 1){
					if(player.canMoveLeft==false){
						player.canMoveLeft= true;
					}
					if(player.canMoveRight==false){
						player.canMoveRight= true;
					}
				}
			}

		}



	}

	String blstring;
	Rect blsCol2 = new Rect();
	Rect areaX2;

	public List<Blocks> findBlocksInArea( Rect area ){

		List<Blocks> blocklist;
		blocklist = new ArrayList<Blocks>();

		//Make area 2x bigger
		areaX2 = new Rect(area.left - area.width(),area.top - area.height(),area.right+area.width(),area.bottom+area.height()); 
		for(Blocks bls : block){
			if(bls.draw == true){
				Rect blsCol =  bls.Collision();
				int left = blsCol.left -cameraX;
				int right = blsCol.right -cameraX;
				int top = blsCol.top;
				int bottom = blsCol.bottom;
				blsCol = new Rect(left,top,right,bottom);
				if(Rect.intersects(blsCol, areaX2)){
					blsCol2 = blsCol;

					blocklist.add(bls);
				}
			}

		}

		return blocklist;
	}

	public List<ScreenObject> findObjectsinArea( Rect area, boolean findBlocks ){

		List<ScreenObject> objList;
		objList = new ArrayList<ScreenObject>();

		Rect area2;


		area2 = new Rect(area.left - area.width(),area.top - area.height(),area.right+area.width(),area.bottom+area.height()); 
		for(ScreenObject blocks : block){
			if(blocks.type == INVENTORY_ITEM || findBlocks == true){
				Rect blsCol =  blocks.Collision();
				int left = blsCol.left -cameraX;
				int right = blsCol.right -cameraX;
				int top = blsCol.top;
				int bottom = blsCol.bottom;
				blsCol = new Rect(left,top,right,bottom);
				if(Rect.intersects(blsCol, area2)){

					objList.add(blocks);
				}
			}
		}
		for(ScreenObject trees : tree){
			if(trees.isOnScreen()){
				Rect blsCol =  trees.Collision();
				int left = blsCol.left -cameraX;
				int right = blsCol.right -cameraX;
				int top = blsCol.top;
				int bottom = blsCol.bottom;
				blsCol = new Rect(left,top,right,bottom);
				if(Rect.intersects(blsCol, area2)){

					objList.add(trees);
				}
			}
		}
		for(ScreenObject stones : stone){
			if(stones.isOnScreen()){
				Rect blsCol =  stones.Collision();
				int left = blsCol.left -cameraX;
				int right = blsCol.right -cameraX;
				int top = blsCol.top;
				int bottom = blsCol.bottom;
				blsCol = new Rect(left,top,right,bottom);
				if(Rect.intersects(blsCol, area2)){

					objList.add(stones);
				}
			}

		}
		for(ScreenObject npcs : npc){
			if(npcs.isOnScreen()){
				Rect blsCol =  npcs.Collision();
				int left = blsCol.left -cameraX;
				int right = blsCol.right -cameraX;
				int top = blsCol.top;
				int bottom = blsCol.bottom;
				blsCol = new Rect(left,top,right,bottom);
				if(Rect.intersects(blsCol, area2)){

					objList.add(npcs);
				}
			}
		}

		return objList;
	}

	public void Draw(String screen){


		this.screen = screen;
		levelView.loaded = true;
		levelView.running = true;


	}

	public int getDistanceX(Rect object1, Rect object2){	//ob1 should be player, ob2 should be other

		int distance = 5;
		String object1Direction = LEFT;

		if(object1.centerX() > object2.centerX()-cameraX){

			object1Direction = RIGHT;

		}

		if(player.movingDir == LEFT){	//if object1 is to the right of object2
			if(object2.centerY() < (object1.bottom-5))
				distance = (object1.centerX() - (object1.width()/2)) - ((object2.centerX()-cameraX) + (object2.width()/2));

		}else if(player.movingDir == RIGHT){					//if object1 is to the left of object2
			if(object2.centerY() < (object1.bottom-5))
				distance = (object1.centerX() + (object1.width()/2)) - ((object2.centerX()-cameraX) - (object2.width()/2));

		}
		if(distance < 0){
			distance = distance *-1;
		}
		return distance;

	}

	public int getDistanceY(Rect object1, Rect object2){

		int distance = 0;
		String object1Direction = UP;

		if(object1.centerY() > object2.centerY()){

			object1Direction = DOWN;

		}

		if(object1Direction == UP){	//if object1 is above of object2

			distance = (object1.centerY() + (object1.height()/2)) - (object2.centerY() - (object2.height()/2));

		}else{							//if object1 is below of object2

			distance = (object1.centerY() - (object1.height()/2)) - (object2.centerY() + (object2.height()/2));

		}
		if(distance<0){
			distance = 0;
		}


		return distance;

	}

	public boolean colIntersect(int x1, int y1, int size1, int x2, int y2, int size2){
		boolean xhit = false;
		boolean hit = false;
		int top1 = y1;
		int bottom1 = y1+size1;
		int left1 = x1;
		int right1 = x1+size1;
		int top2 = y2;
		int bottom2 = y2+size2;
		int left2 = x2;
		int right2 = x2+size2;

		if((right1>=left2)&&(right1 <= right2)){
			xhit = true;
			//toast("right");
		}

		if((left1<=right2)&&(left1 >= left2)){
			xhit = true;
			//toast("left");
		}	
		if((bottom1>=top2)&&(bottom1 <= bottom2)){
			if(xhit == true){
				hit = true;
				//toast("bottom");
			}
		}


		if((top1<=bottom2)&&(top1 >= top2)){
			if(xhit == true){
				hit = true;
				//toast("top");
			}
		}	



		return hit;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();


		if (event.getAction()==MotionEvent.ACTION_DOWN) {

			if(screen == WORLD ){
				if(touchingCursor(x,y)==true){
					cursorDown = true;
					cursorDownX = (int) x;
					cursorDownY = (int) y;

				}
			}

			if(touchingButton(x,y).contains("button" + JOURNAL)){
				openJournal();
			}
			if(screen == WORLD){
				if(touchingButton(x,y).contains("button" + INVENTORY)){
					openInventory();
				}
			}else{
				if(touchingButton(x,y).contains("button" + INVENTORY+EQUIP)){
					
					player.weapon.setWeapon(selectedItem);
					equippedItem = selectedItem;
					toast(selectedItem.displayName + " equiped",30);
				}else if(touchingButton(x,y).contains("button" + INVENTORY+TOSS)){
					
					player.inventory.get(player.inventory.indexOf(selectedItem)).toss();
					toast(selectedItem.displayName + " tossed",30);
				}
			}
			if(touchingButton(x,y).contains("button" + QUIT)){
				save();
				//finish();
			}
			if(touchingButton(x,y).contains("button" + ATTACK)){
				if(player.canAttack == true){
					if(player.weapon.isHitting == true){

						player.weapon.isHitting = false;
					}

					player.attack(findObjectsinArea(player.weapon.Collision(), false), cameraX);

					if(!selectedItem.type.equals(Weapon.WEAPON)){


						if(selectedItem.optionString.equals( NPC.ANIMAL )){
							player.eat( selectedItem.value );
							player.inventory.get(player.inventory.indexOf(selectedItem)).consume();

							effects.width = player.weapon.width;
							effects.height = player.weapon.height;
							ScreenObject blood = effects.blood();
							blood.width = player.weapon.width;
							blood.height = player.weapon.height;
							if(player.getDirection().equals(LEFT)){
								blood.x = (player.getX()) + cameraX;

							}else{
								blood.x = (player.getX() + player.size/2) + cameraX;

							}
							blood.y = player.weapon.y;
							blood.animation.start(0, 0);
							screenEffects.add(blood);

						}
					}
					handler.postDelayed(attackDelay, 1000);
				}
			}
			if(touchingButton(x,y).contains("item" + INVENTORY)){
				String name = touchingButton(x,y);
				name = name.replace("item" + INVENTORY, "");
				int itemId = Integer.parseInt(name);
				selectedItem = player.inventory.get(itemId);
			}
			if(screen == WORLD){
				if(touchingCursor(x,y)==false){
					if(player.canAttack == true && holding == false){

						if(player.weapon.type.equals(INVENTORY_ITEM)){
							if(player.inventory.get(player.inventory.indexOf(selectedItem)).getAmount() > 0){
								Blocks item = new Blocks(INVENTORY_ITEM,blocksRightCount,0,block_size, (int)x - (block_size/2) + cameraX,(int)y - (block_size/2) ,true, getResources(), this, selectedItem);
								item.isSolid = selectedItem.isSolid;
								block.add(item);
								holdingBlock = block.size()-1;
								holding = true;
								player.canAttack = false;
								player.inventory.get(player.inventory.indexOf(selectedItem)).consume();
								handler.postDelayed(attackDelay, 1000);

							}else{

								//No more of item, give different weapon
								//player.weapon.setWeapon(Weapon.AXE);
							}
						}


						if( player.weapon.isThrowable == true ){
							player.weapon.throwWeapon( new IntSize( x, y ) );
						}


					}
				}
			}
		}

		if (event.getAction()==MotionEvent.ACTION_UP) {

			cursorDown = false;
			player.canJump = true;
			player.movingDir = NO;

			if(screen == WORLD){
				if(holding == true){
					int countBl = 0;
					Rect Col =  block.get(holdingBlock).Collision();
					Col = new Rect(Col.left - cameraX + block.get(holdingBlock).getSize()/5,Col.top+ block.get(holdingBlock).getSize()/5,Col.right - block.get(holdingBlock).getSize()/5 - cameraX,Col.bottom - block.get(holdingBlock).getSize()/5);
					if(	block.get(holdingBlock).isSolid == true ){
						for(Blocks bl : block){
							if(!bl.equals(block.get(holdingBlock))){


								Rect blCol = bl.Collision();
								blCol = new Rect(blCol.left - cameraX,blCol.top,blCol.right - cameraX,blCol.bottom);

								if(!Rect.intersects(Col, blCol)){
									countBl ++;
								}
							}
						}
						for(Tree bl : tree){
							Rect blCol = bl.Collision();
							blCol = new Rect(blCol.left - cameraX,blCol.top,blCol.right - cameraX,blCol.bottom);

							if(!Rect.intersects(Col, blCol)){
								countBl ++;
							}
						}
						for(Stone bl : stone){
							Rect blCol = bl.Collision();
							blCol = new Rect(blCol.left - cameraX,blCol.top,blCol.right - cameraX,blCol.bottom);

							if(!Rect.intersects(Col, blCol)){
								countBl ++;
							}
						}
						for(NPC bl : npc){
							Rect blCol = bl.Collision();
							blCol = new Rect(blCol.left - cameraX,blCol.top,blCol.right - cameraX,blCol.bottom);

							if(!Rect.intersects(Col, blCol)){
								countBl ++;
							}
						}
						if(countBl >= (block.size() + tree.size() + stone.size() + npc.size())-1){

						}else{

							player.inventory.get(player.inventory.indexOf(selectedItem)).addAmount(1);
							block.remove(holdingBlock);
							toast("Could not place",5);
						}
					}
					holding = false;
					holdingBlock = 0;
				}
			}

		}

		if (event.getAction()==MotionEvent.ACTION_MOVE) {

			if(screen == WORLD){
				if( holding == true ){
					if( holdingBlock > 0 ){

						if(x < (screen_width/2)){

							block.get(holdingBlock).setX((int) (x  + (block_size) + cameraX));
						}else{

							block.get(holdingBlock).setX((int) (x  - (block_size*2) + cameraX));
						}

						block.get(holdingBlock).setY((int) (y));

					}

				}
			}


		}
		if(screen == WORLD ){
			if(touchingCursor(x,y)==true){
				if(cursor(x,y)==RIGHT){
					player.setDirection(RIGHT);
				}else{

					player.setDirection(LEFT);
				}
				playerHandler.removeCallbacks(movePlayer);
				playerHandler.post(movePlayer);

			}

		}

		return super.onTouchEvent(event);

	}

	public String touchingButton( float x, float y){

		String buttonName = "NULL";

		for ( ButtonArea button : buttonAreas){
			if(x >= button.getX() && x <= button.getX()+button.getWidth() && y >= button.getY() && y<= button.getY()+button.getHeight()){

				buttonName = button.name;	

			}
		}

		if(buttonName != "NULL"){
			//toast(buttonName);
		}

		return buttonName;
	}

	public boolean touchingCursor(float x, float y){
		boolean isTouching=false;

		if(x >= cursorX && x <= cursorX+cursorSize && y >= cursorY && y<= cursorY+cursorSize){
			isTouching = true;
		}

		return isTouching;
	}

	public String cursor( float x, float y){

		cursorUserX = x;
		cursorUserY = y;
		String dir = player.getDirection();
		if(cursorDown == true){
			if(x< cursorDownX){
				//Move left

				dir = LEFT;
				player.movingDir = LEFT;

			}else{
				//Move right

				dir = RIGHT;
				player.movingDir = RIGHT;

			}
			if(y < (cursorDownY-20)){
				player.jump();
				lastJumpY = (int) y;
				player.canJump = false;
			}
			if(y > lastJumpY + 20){
				player.canJump = true;
			}
		}
		return dir;


	}

	private Runnable movePlayer = new Runnable() {  

		public void run() {  
			if( cursorDown == true){
				if(player.getDirection() ==RIGHT){
					if(player.canMoveRight==true){
						if(player.getX() <= screen_width-playerEdgeWidth-player.size){

							player.moveX(1);

						}else{
							cameraX++;

							//block.get(i).moveX(-1);
							if(block.get(block.size()-1).getX()<= screen_width+cameraX){
								addBlock(RIGHT);

							}


						}

					}
					player.animation.start(0, 0);
				}else{

					if(player.canMoveLeft==true){
						if(player.getX() >= playerEdgeWidth){
							player.moveX(-1);
						}
						else{
							cameraX--;
							//block.get(i).moveX(-1);
							if(block.get(block.size()-1).getX()>0-cameraX){
								addBlock(LEFT);
							}
						}
					}

				}


				playerHandler.postDelayed(movePlayer,10);
			}    
		}
	};

	private final Runnable attackDelay = new Runnable() {
		@Override
		public void run() {

			player.canAttack = true;

		}
	};


	public void addBlock(String side){

		if(side==RIGHT)
		{


			int height = getNextHeight();

			addRandomBlock(height);




		}
		else
		{

			//block.add(new Blocks(biome.surfaceBlock,-blocksLeftCount,1,block_size,(nextBlockLeftX),height_bottom-block_size,true));
			//nextBlockLeftX = -(blocksLeftCount*block_size);
			//blocksLeftCount++;



		}	
	}

	public int getNextHeight(){
		Random random = new Random();
		int randint = random.nextInt(3);
		if(nextBlockHeight == 1){
			randint = 1;
			int randFlatness = random.nextInt(biome.flatness);
			if(randFlatness == 0){
				nextBlockHeight = 0;
			}
		}

		int height = 0;
		if(randint == 0){
			//Go down 1 level
			if(lastBlockHeight>0){
				height = lastBlockHeight - 1;
				nextBlockHeight = 1;
			}

		}
		if(randint == 1){
			//Stay same level
			height = lastBlockHeight;
			if( lastBlock != WATER){

				if(nextBlockHeight == 1){
					//nextBlockHeight = 1;
					addTree(height);
					addStone(height);
				}

			}
		}
		if(randint == 2){
			//Go up 1 level
			if(lastBlockHeight<MAXBLOCKHEIGHT){
				height = lastBlockHeight + 1;
				nextBlockHeight = 1;
			}else{
				height = lastBlockHeight;
			}
		}
		return height;
	}

	public void addRandomBlock( int height){
		String BType = biome.surfaceBlock;
		Random random = new Random();

		if(height == 0){	//Water
			int randWater = random.nextInt(biome.waterChance);
			if(randWater == 0){
				BType = WATER;
			}else{
				if(lastBlock == WATER){
					int randWaterLength = random.nextInt(biome.waterLength);

					if(randWaterLength == 0){
						BType = WATER;
						if( random.nextInt(biome.fishChance) == 1 ){

							spawnFish((nextBlockRightX)+cameraX,height_bottom-(block_size*height));
						}
					}else{
						BType = biome.surfaceBlock;
					}
				}else{
					BType = biome.surfaceBlock;
				}
			}
		}else{
			BType = biome.surfaceBlock;
		}


		if(BType == biome.surfaceBlock){
			int snowAdj = 0;
			if(biome.surfaceBlock == SNOW){
				snowAdj = SNOW_ADJ;
			}
			block.add(new Blocks(biome.surfaceBlock,blocksRightCount,height,block_size,(nextBlockRightX)+cameraX,height_bottom-(block_size*height) - snowAdj ,true, getResources(), this));
			lastBlock = biome.surfaceBlock;
		}else if(BType == WATER){
			if(lastBlockHasObject == false){
				block.add(new Blocks(WATER,blocksRightCount,height,block_size,(nextBlockRightX)+cameraX,height_bottom-(block_size*height),false, getResources(), this));
				lastBlock = WATER;
			}
		}

		lastBlockHasObject = false;

		if( random.nextInt(biome.rabbitChance) == 0 ){
			spawnRabbit((nextBlockRightX)+cameraX,height_bottom-(block_size*height));
		}


		for (int i = 0; i < height; i++) {
			block.add(new Blocks(biome.underBlock,blocksRightCount,i,block_size,(nextBlockRightX)+cameraX,height_bottom-(block_size*i),true, getResources(), this));

		}
		nextBlockRightX = (blocksRightCount*block_size);
		lastBlockHeight = height;
		//blocksRightCount++;

	}

	public void addTree(int height){

		Random random = new Random();
		int randTree = random.nextInt(biome.treeChance);
		int xPos = (nextBlockRightX +cameraX) - (block_size/2) - Tree.GIVEWAY;
		IntSize size = getPercentSize(Tree.P_WIDTH, Tree.P_HEIGHT);
		int swidth = (int)(size.width);
		int sheight =  (int)(size.height);

		boolean flip = false;
		if (random.nextInt(3) == 0){
			flip = true;
		}
		if(randTree == 0){
			int woodVal = 5;
			woodVal += random.nextInt(10);
			if(biome.treeType == TREE_DEFAULT){
				tree.add(new Tree(100, GRASS,xPos,height_bottom-(block_size*height)-sheight,swidth,sheight,woodVal,flip, getResources(), this));
			}
			if(biome.treeType ==  TREE_SNOW){
				tree.add(new Tree(100, SNOW,xPos,height_bottom-(block_size*height)-sheight,swidth,sheight,woodVal,flip, getResources(), this));
			}
			int randBird = random.nextInt(biome.birdChance);
			if(randBird == 0){
				Bird bird = new Bird(getResources(),this);
				Point branch[] = {new Point(xPos + (swidth/2),height_bottom-(block_size*height)-sheight),
						new Point(xPos + (swidth/3),height_bottom-(block_size*height)-sheight + (sheight/6))};
				Random r = new Random();
				bird.treeId = tree.size();
				bird.spawn(branch[r.nextInt(2)]); 
				npc.add( bird );

			}
			lastBlockHasObject = true;
		}

	}

	public void addStone(int height){

		Random random = new Random();
		int randStone = random.nextInt(biome.stoneChance);
		int xPos = (nextBlockRightX +cameraX) - (block_size/2) - Stone.GIVEWAY;
		IntSize size = getPercentSize(Stone.P_WIDTH, Stone.P_HEIGHT);
		int swidth =  (int)(size.width);
		int sheight =  (int)(size.height);
		boolean flip = false;
		if (random.nextInt(3) == 0){
			flip = true;
		}
		if(randStone == 0){
			int stoneVal = 3;
			stoneVal += random.nextInt(7);
			if(biome.surfaceBlock == GRASS){
				stone.add(new Stone(200, STONE,xPos,height_bottom-(block_size*height)-sheight,sheight,sheight,stoneVal,flip, getResources(), this));
			}
			if(biome.surfaceBlock == SNOW){
				stone.add(new Stone(200, SILVER,xPos,height_bottom-(block_size*height)-sheight,sheight,sheight,stoneVal,flip, getResources(), this));
			}
			lastBlockHasObject = true;
		}

	}

	public void spawnRabbit( int x, int y ){

		IntSize size = getPercentSize(Rabbit.P_WIDTH, Rabbit.P_HEIGHT);
		int swidth = (int)(size.width);
		int sheight =  (int)(size.height);

		Rabbit rabbit = new Rabbit(getResources(),this);
		Point loc[] = {new Point(x,y-sheight),
				new Point(x - swidth,y-sheight),new Point(x + swidth,y-sheight)};
		Random r = new Random();

		rabbit.spawn(loc[r.nextInt(3)]); 
		rabbit.jumpHeight = getPercentSize(Rabbit.JUMP_HEIGHT_P);
		npc.add( rabbit );
	}

	public void spawnFish( int x, int y ){

		IntSize size = getPercentSize(Fish.P_WIDTH, Fish.P_HEIGHT);
		int swidth = (int)(size.width);
		int sheight =  (int)(size.height);
		Random r = new Random();

		Fish fish = new Fish(getResources(),this,NPC.ANIMAL_FISH_BLUEGILL);

		if( r.nextInt(biome.fishChance) == 0 ){	//Salmon
			swidth *=2;
			sheight += (sheight/2);
			fish.width = swidth;
			fish.height = sheight;
			fish.setupSalmon(getResources(), swidth, sheight);
		}

		Point loc[] = {new Point(x,y),
				new Point(x - swidth,y + (sheight/2))};

		fish.spawn(loc[r.nextInt(2)]); 
		fish.jumpHeight = getPercentSize(Fish.JUMP_HEIGHT_P);
		npc.add( fish );
	}

	public boolean isOffScreen(Blocks bl){

		boolean isoffscreen = false;

		if(bl.getX()<=((screen_width+cameraX)+bl.getSize())&bl.getY()>=((screen_height)+bl.getSize())){

			isoffscreen = true;

		}




		return isoffscreen;

	}

	public static boolean isOnScreen( int x, int y, int width, int height, int cameraX, Player player ){

		boolean onScreen = true;
		if(y < (0 - height) || y > screen_height || x - cameraX < (player.getX() - screen_width) || x - cameraX > (player.getX() + screen_width)){
			onScreen = false;
		}

		return onScreen;
	}

	public void rebuildInventory( List<InventoryItem> inventory){
		List<InventoryItem> removeList;
		removeList = new ArrayList<InventoryItem>();

		for( InventoryItem item : inventory){
			String name = item.getName();
			String type = item.type;

			for (int i = 0; i < inventory.size(); i++) {
				if( i != inventory.indexOf(item)){
					InventoryItem tmp = inventory.get(i);
					boolean isSame = false;
					if((name.equals(tmp.getName()) && type.equals(tmp.type)) || name.equals("Wood ") && tmp.getName().equals("Wood")){
						//toast("Names("+name + "," + tmp.getName() +")" + " Types(" + type +"," + tmp.getType() + ")");
						isSame = true;
					}
					if(isSame == true){

						tmp.otherInt = inventory.indexOf(inventory.get(i));
						item.setAmount(item.getAmount() + tmp.getAmount());

						inventory.get(i).setName("null");
						inventory.get(i).setAmount(0);
						removeList.add(inventory.get(i));
					}
				}
			}

			item.setImage(retrieveItemImage(item.getName()));
		}

		for( InventoryItem i : removeList ){
			inventory.remove(i);
		}
		removeList.clear();


	}
	public Bitmap retrieveItemImage( String name ){
		Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.blank);

		if(name.equals("Wood")){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.woodplank);

		}
		if(name.equals(Blocks.WOOD_WALL)){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.woodwall);

		}
		if(name.equals("Stone")){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.rock);

		}
		if(name.equals(NPC.ANIMAL_BIRD_DOVE)){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.bird);

		}
		if(name.equals(NPC.ANIMAL_RABBIT)){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.bunny1);

		}
		if(name.equals(NPC.ANIMAL_FISH_BLUEGILL)){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.fish);

		}
		if(name.equals(NPC.ANIMAL_FISH_SALMON)){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.salmon);

		}
		if(name.equals(Weapon.AXE)){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.axe);

		}
		if(name.equals(Weapon.PICKAXE)){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.pickaxe);

		}
		if(name.equals(Weapon.TORCH)){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.torch);

		}
		if(name.equals(Weapon.ROCK)){
			image = BitmapFactory.decodeResource(getResources(), R.drawable.rockweapon);

		}

		return image;
	}
	public void save(){

		prefEditor.putString("Home", home_biome);
		prefEditor.putFloat("Time", world_time);
		prefEditor.putString("Moon", levelView.sun.x + "," + levelView.sun.y);
		prefEditor.commit();

		playEditor.putString("Name", player_name);
		playEditor.putInt("Health",player.health);
		playEditor.putInt("Hunger",player.hunger);	
		playEditor.putInt("Thirst",player.thirst);
		playEditor.putInt("Survived",player.survived);

		//Save Inventory
		String value = "{";
		for( InventoryItem item : player.inventory ){
			value += "[" + item.toString() + "]";
		}
		value += "}";
		playEditor.putString("Inventory", value);
		playEditor.commit();
		toast(value);

		//Save cabin area
		String value1 = "{";
		for( Blocks blocki : block ){
			if(blocki.type == INVENTORY_ITEM){
				if((blocki.getX() - cameraX) < screen_width - cameraX){

					value1 += "[" + blocki.toString() + "]";
				}
			}
		}
		value1 += "}";
		prefEditor.putString("Cabin", value1);
		prefEditor.commit();
		toast(value1);

		finish();

	}

	public float getTime(){

		float time = world_time;
		return time;
	}

	public String getTimeString(){

		String time = getTime() + "";
		time = time.substring(0, 5);
		return time;
	}
	public void openWorld(){

		//TODO Correct sizes using getPercentSize()
		buttonAreas.clear();
		buttonAreas.add(new ButtonArea(buttonAreas.size(),0,0,BUTTON_SIZE,BUTTON_SIZE,"button"+JOURNAL));
		buttonAreas.add(new ButtonArea(buttonAreas.size(),screen_width - BUTTON_SIZE,0,BUTTON_SIZE,BUTTON_SIZE,"button"+INVENTORY));
		IntSize size = getPercentSize(ButtonArea.P_WIDTH,ButtonArea.P_WIDTH);
		int width = (int)(size.width);
		int height =  (int)(size.height);
		buttonAreas.add(new ButtonArea(buttonAreas.size(),screen_width - width - 10, screen_height - height - 10,width,height,"button"+ATTACK));

		levelView.setupHud();
		Draw(WORLD);

	}

	public void openJournal(){

		//TODO Correct sizes using getPercentSize()
		DEV_MODE = true;
		toast("DEV MODE ON",5);
		buttonAreas.clear();
		biome.setBiome(GRASS);
		Draw(JOURNAL);

	}

	public void openInventory(){

		//TODO Correct sizes using getPercentSize()
		buttonAreas.clear();
		//
		DEV_MODE = false;

		biome.setBiome(SNOW);
		//
		rebuildInventory(player.inventory);
		IntSize start = getPercentSize(1, 1);
		IntSize size = getPercentSize(InventoryItem.P_BUTTON_WIDTH, InventoryItem.P_BUTTON_HEIGHT);
		buttonbox = new ArrayList<Rect>();
		int boxWidth = (int) size.width;
		int boxHeight = (int) size.height;
		box = new Rect ( 10,10, boxWidth , boxHeight);
		int bX = (int) start.width;
		int bY = boxHeight;
		for(InventoryItem buttons : player.inventory){
			Rect buttonA = new Rect(bX, bY,boxWidth , bY + boxHeight);
			buttonbox.add(buttonA);
			ButtonArea item = new ButtonArea(buttonAreas.size(),bX,bY,boxWidth,boxHeight,"item"+INVENTORY+player.inventory.indexOf(buttons));

			buttonAreas.add(item);
			bY+=boxHeight;
		}
		//Equip								 //size					adj					more adj
		ButtonArea equip = new ButtonArea(buttonAreas.size(),screen_width - getPercentSize(12) - getPercentSize(17) - (getPercentSize(1)/2),(int) (screen_height - boxHeight - (start.height*5)),getPercentSize(12),boxHeight,"button"+INVENTORY+EQUIP);
		buttonAreas.add(equip);
		Rect buttonA = new Rect(equip.x, equip.y,equip.x + equip.width , equip.y + equip.height);
		buttonbox.add(buttonA);

		//Toss								 //size					adj					more adj
		ButtonArea toss = new ButtonArea(buttonAreas.size(),screen_width - getPercentSize(12) - getPercentSize(5) - (getPercentSize(1)/2),(int) (screen_height - boxHeight - (start.height*5)),getPercentSize(12),boxHeight,"button"+INVENTORY+TOSS);
		buttonAreas.add(toss);
		Rect buttonB = new Rect(toss.x, toss.y,toss.x + toss.width , toss.y + toss.height);
		buttonbox.add(buttonB);
		Draw(INVENTORY);

	}
	public Rect box;
	public List<Rect> buttonbox;
	public void openCabin(){

		buttonAreas.clear();
		Draw(CABIN);

	}

	public void openPause(){

		//TODO Correct sizes using getPercentSize()
		buttonAreas.clear();
		int bX = screen_width/2 - (screen_width/4);
		int bY = screen_height/2 + (screen_height/8);
		buttonAreas.add(new ButtonArea(buttonAreas.size(),bX,bY,BUTTON_SIZE*4,BUTTON_SIZE,"button"+QUIT));

		Draw(PAUSE);

	}

	public void toast( String message, int seconds ){

		for(int i = 0; i < seconds; i++){
			toastLog.add( message );
		}
	}

	public void toast( String message ){

		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

	}

	public IntSize getPercentSize( float percentWidth, float percentHeight ){	//x = width, y = height

		IntSize size = new IntSize();
		if( percentWidth > 100 ){
			percentWidth = 100;
		}
		if( percentHeight > 100 ){
			percentHeight = 100;
		}
		size.width = screen_width / (100/percentWidth);		//width
		size.height = screen_height / (100/percentHeight);	//height

		return size;
	}

	public static int getPercentSize( int percentSize ){	// screen width

		int size = 0;
		if( percentSize > 100){
			percentSize = 100;
		}

		size = screen_width / (100/percentSize);


		return size;
	}

	public static int getPercentSize( float percentSizeH ){	// screen height

		int size = 0;
		if( percentSizeH > 100){
			percentSizeH = 100;
		}

		size = (int) (screen_height / (100/percentSizeH));


		return size;
	}

}
