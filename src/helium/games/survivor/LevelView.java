package helium.games.survivor;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class LevelView extends SurfaceView implements Runnable {

	public static final int PERCENT_BAR_W = 10;
	public static final float PERCENT_BAR_H = 1;

	SurfaceHolder surfaceHolder;
	Thread levelThread = null;
	public Level level;
	private Handler handler;

	public int canvasH = 1;
	public int canvasW = 1;

	public boolean loaded = false;
	public boolean running = false;
	Bitmap attackHud;
	Paint paint;
	Bitmap box;
	Bitmap snow;
	Bitmap grass;
	Bitmap dirt;
	Bitmap water;
	Bitmap box1;
	Bitmap tree;
	Bitmap treeGrass;
	Bitmap treeSnow;
	Bitmap stoneObj;
	Bitmap stone;
	Bitmap stone2;
	Bitmap silver;
	Bitmap silver2;
	Bitmap glow;

	public Point sun = new Point(0,0);

	Paint cursorP;
	int fps = 0;
	int lastfps = 0;
	Handler hd;
	boolean runfps = true;
	Rect bodyCol;
	Rect bodyColX2;

	int attackint = 0;
	int journalint = 0;
	int inventoryint = 0;

	public LevelView(Context context, int swidth, int sheight, Level lv) {
		super(context);
		level = lv;
		surfaceHolder =  getHolder();
		handler = new Handler();
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(level.getPercentSize(5.0f));
		paint.setStrokeWidth(1);
		paint.setStyle(Paint.Style.STROKE);
		hd = new Handler();
		level.world_bg  = Bitmap.createScaledBitmap(level.world_bg,swidth , sheight,true);
		level.stars_bg  = Bitmap.createScaledBitmap(level.stars_bg,swidth , sheight/2,true);
		glow = BitmapFactory.decodeResource(getResources(), R.drawable.glow);
		glow = Bitmap.createScaledBitmap(glow, level.screen_width/2, level.screen_height/2, true);
		sun.x = 0 - glow.getWidth() + (glow.getWidth()/3);
		sun.y = level.screen_height/2;
		attackHud = BitmapFactory.decodeResource(getResources(), R.drawable.attackhud);
		setupBlocks();
		setupTrees();
		setupStones();

		box = snow;
		tree = treeGrass;


		level.player.image_idle  = Bitmap.createScaledBitmap(level.player.image_idle,level.player.size , level.player.size,true);
		level.cursorBG  = Bitmap.createScaledBitmap(level.cursorBG, level.cursorSize , level.cursorSize,true);
		cursorP = new Paint();
		cursorP.setAlpha(level.cursorAlpha);
		bodyCol = level.player.bodyCollision();
		bodyColX2 = new Rect(bodyCol.left - bodyCol.width(),bodyCol.top - bodyCol.height(),bodyCol.right+bodyCol.width(),bodyCol.bottom+bodyCol.height());

	}

	private Runnable clearfps = new Runnable() {  
		public void run() {  
			lastfps = fps;
			fps=0;
			runfps=true;
		}
	};


	private void setupBlocks(){
		snow = BitmapFactory.decodeResource(getResources(), R.drawable.snowtop);
		snow = Bitmap.createScaledBitmap(snow, level.block_size, level.block_size+Level.SNOW_ADJ, true);

		grass = BitmapFactory.decodeResource(getResources(), R.drawable.grass);
		grass = Bitmap.createScaledBitmap(grass, level.block_size, level.block_size, true);

		dirt = BitmapFactory.decodeResource(getResources(), R.drawable.dirt);
		dirt = Bitmap.createScaledBitmap(dirt, level.block_size, level.block_size, true);

		water = BitmapFactory.decodeResource(getResources(), R.drawable.water);
		water = Bitmap.createScaledBitmap(water, level.block_size, level.block_size, true);

	}

	private void setupTrees(){
		IntSize size = level.getPercentSize(Tree.P_WIDTH, Tree.P_HEIGHT);
		int width =  (int)(size.width);
		int height =  (int)(size.height);
		treeGrass = BitmapFactory.decodeResource(getResources(), R.drawable.treenew1);
		treeGrass = Bitmap.createScaledBitmap(treeGrass, width, height, true);

		treeSnow = BitmapFactory.decodeResource(getResources(), R.drawable.treesnow);
		treeSnow = Bitmap.createScaledBitmap(treeSnow, width, height, true);
	}

	private void setupStones(){

		stoneObj = BitmapFactory.decodeResource(getResources(), R.drawable.rock);
		stoneObj = Bitmap.createScaledBitmap(stoneObj, Stone.WIDTH, Stone.HEIGHT, true);

		stone = BitmapFactory.decodeResource(getResources(), R.drawable.rock);
		stone = Bitmap.createScaledBitmap(stone, Stone.WIDTH, Stone.HEIGHT, true);

		stone2 = BitmapFactory.decodeResource(getResources(), R.drawable.rock2);
		stone2 = Bitmap.createScaledBitmap(stone2, Stone.WIDTH, Stone.HEIGHT, true);

		silver = BitmapFactory.decodeResource(getResources(), R.drawable.silver);
		silver = Bitmap.createScaledBitmap(silver, Stone.WIDTH, Stone.HEIGHT, true);

		silver2 = BitmapFactory.decodeResource(getResources(), R.drawable.silver2);
		silver2 = Bitmap.createScaledBitmap(silver2, Stone.WIDTH, Stone.HEIGHT, true);

	}

	public void setupHud(){

		for(ButtonArea btns : level.buttonAreas){
			if(btns.name.equals("button" + level.ATTACK))
				attackint = btns.id;
			if(btns.name.equals("button" + level.JOURNAL))
				journalint = btns.id;
			if(btns.name.equals("button" + level.INVENTORY))
				inventoryint = btns.id;
		}
		int attackWidth = level.buttonAreas.get(attackint).width;
		int attackHeight = level.buttonAreas.get(attackint).height;
		attackHud = BitmapFactory.decodeResource(getResources(), R.drawable.attackhud);
		attackHud = Bitmap.createScaledBitmap(attackHud, attackWidth , attackHeight, true);

	}

	@Override
	public void run() {
		while(running){
			if(surfaceHolder.getSurface().isValid()){
				if(level.canDraw==true){
					Canvas canvas = surfaceHolder.lockCanvas();

					if(level.screen == Level.WORLD){
						DrawWorld(canvas);
					}else if(level.screen == level.CABIN){
						DrawCabin(canvas);
					}else if(level.screen == level.INVENTORY){
						DrawInventory(canvas);
					}else if(level.screen == level.JOURNAL){
						DrawJournal(canvas);

					}else if(level.screen == level.LOADING){
						DrawLoading(canvas);

					}else if(level.screen == level.PAUSE){
						DrawPause(canvas);

					}else{
						//ERROR
						toast("Error loading screen");
						running = false;
					}

					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}


	public void LevelView_OnResume() {

		Random random = new Random();
		surfaceHolder = getHolder();
		running = true;
		levelThread = new Thread(this);
		levelThread.start();
		//canvasW = surfaceHolder.lockCanvas().getWidth();
		//canvasH = surfaceHolder.lockCanvas().getHeight();
		if(loaded==true){
			levelThread = new Thread();
			levelThread.start();
		}



	}

	public void LevelView_OnPause() {
		boolean retry = true;
		running = false;
		while(retry){
			try {
				levelThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void DrawWorld(Canvas canvas){


		//Draw background
		canvas.drawBitmap(level.world_bg,0,0, null);
		//

		//Draw stars

		Paint star = new Paint();
		star.setColor(Color.WHITE);
		star.setAntiAlias(true);

		double alpha = 0;
		alpha = level.getTime() - 12;
		alpha = (double) Math.pow(alpha, 2);
		star.setAlpha((int) alpha*2);

		if( level.getTime() > 22 || level.getTime() < 2 ){
			star.setAlpha(254);

		}
		canvas.drawBitmap(level.stars_bg,0,0, star);
		//canvas.drawText("Alpha Stars: " + star.getAlpha(), 10, 140, paint);


		//

		//Draw blocks

		for (int i = 0; i < level.block.size(); i++) {
			if(isOnScreen(level.block.get(i)) == true ){
				if(level.block.get(i).isSolid == false){
					if(!level.block.get(i).getType().equals(level.WATER)){
						if(level.block.get(i).getType().equals(level.INVENTORY_ITEM)){

							canvas.drawBitmap(level.block.get(i).animation.main, level.block.get(i).getX()-level.cameraX,level.block.get(i).getY(), null);
						}
					}
				}
			}else{
				level.block.get(i).draw = false;
			}
		}

		for (int i = 0; i < level.block.size(); i++) {
			if(isOnScreen(level.block.get(i)) == true){
				level.block.get(i).draw = true;
				if(level.block.get(i).isSolid == true){

					if(level.block.get(i).getType().equals(level.SNOW)){
						box = snow;
					}else if(level.block.get(i).getType().equals(level.GRASS)){
						box = grass;

					}else if(level.block.get(i).getType().equals(level.DIRT)){
						box = dirt;

					}else if(level.block.get(i).getType().equals(level.WATER)){
						box = water;

					}else if(level.block.get(i).getType().equals(level.INVENTORY_ITEM)){


					}else{
						toast("Error - biome");
						running = false;
					}

					if(!level.block.get(i).getType().equals(level.WATER)){
						if(!level.block.get(i).getType().equals(level.INVENTORY_ITEM)){
							canvas.drawBitmap(box, level.block.get(i).getX()-level.cameraX,level.block.get(i).getY(), null);
						}else{

							canvas.drawBitmap(level.block.get(i).animation.main, level.block.get(i).getX()-level.cameraX,level.block.get(i).getY(), null);

						}
					}
				}
			}else{
				level.block.get(i).draw = false;
			}
		}

		//
		//Draw Trees
		try{
			for (int i = 0; i < level.tree.size(); i++) {
				if(level.tree.get(i).isOnScreen()){
					if(level.tree.get(i).animation.playing == false){
						level.tree.get(i).animation.start(0, 0);
					}
					if(level.tree.get(i).getType().equals(level.GRASS)){
						tree = treeGrass;
					}
					if(level.tree.get(i).getType().equals(level.SNOW)){
						tree = treeSnow;
					}

					if(level.tree.get(i).isDead == false){

						if(level.tree.get(i).flip == false){

							canvas.drawBitmap(level.tree.get(i).animation.main, level.tree.get(i).getX()-level.cameraX,level.tree.get(i).getY(), null);


						}else{
							Matrix m = new Matrix();
							m.preScale(-1.0f, 1.0f);
							Bitmap bm = Bitmap.createBitmap(level.tree.get(i).animation.main,0,0, level.tree.get(i).width, level.tree.get(i).height, m, false);

							canvas.drawBitmap(bm, level.tree.get(i).getX()-level.cameraX,level.tree.get(i).getY(), null);




						}


					}

					if(level.tree.get(i).health < level.tree.get(i).maxHealth && level.tree.get(i).health > 0){
						canvas.drawText(level.tree.get(i).health + "/" + level.tree.get(i).maxHealth, level.tree.get(i).getX() -level.cameraX , level.tree.get(i).getY(), paint);
					}
				}else{
					level.tree.get(i).animation.stop();

				}
			}
		}catch( NullPointerException e ){

		}catch( IndexOutOfBoundsException e){

		}
		//

		//Draw stones

		for (int i = 0; i < level.stone.size(); i++) {
			if(level.stone.get(i).isDead == false){
				try{
					if(level.stone.get(i).getType().equals(level.STONE)){			//STONEs

						canvas.drawBitmap(level.stone.get(i).animation.main, level.stone.get(i).getX()-level.cameraX,level.stone.get(i).getY(), null);

					}

					if(level.stone.get(i).getType().equals(level.SILVER)){		//Silvers
						canvas.drawBitmap(level.stone.get(i).animation.main, level.stone.get(i).getX()-level.cameraX,level.stone.get(i).getY(), null);

					}
					/*
			if(level.stone.get(i).flip == false){
				canvas.drawBitmap(stone, level.stone.get(i).getX()-level.cameraX,level.stone.get(i).getY(), null);
			}else{
				Matrix m = new Matrix();
				m.preScale(-1.0f, 1.0f);
				Bitmap bm = Bitmap.createBitmap(stone,0,0, level.stone.get(i).WIDTH, level.stone.get(i).HEIGHT, m, false);

				canvas.drawBitmap(bm, level.stone.get(i).getX()-level.cameraX,level.stone.get(i).getY(), null);


			}*/
					if(level.stone.get(i).health < level.stone.get(i).maxHealth && level.stone.get(i).health > 0){
						canvas.drawText(level.stone.get(i).health + "/" +level.stone.get(i).maxHealth, level.stone.get(i).getX() - level.cameraX, level.stone.get(i).getY(), paint);
					}
				}catch( NullPointerException e){

				}catch( IndexOutOfBoundsException e){

				}
			}
		}

		//

		//Draw inventory items

		for(InventoryItem items: level.player.inventory){
			try{
				if( items.isInInventory == false){
					canvas.drawBitmap(Bitmap.createScaledBitmap(items.image, items.width, items.width,true), items.x - level.cameraX, items.y, null);
				}
			}catch( NullPointerException e ){

			}
		}
		//

		//Draw Player
		if(level.player.getDirection()==Level.RIGHT){
			canvas.drawBitmap(level.player.animation.main,level.player.getX(),level.player.getY(), null);
		}else{
			Matrix m = new Matrix();
			m.preScale(-1.0f, 1.0f);
			Bitmap bm = Bitmap.createBitmap(level.player.animation.main,0,0, level.player.size, level.player.size, m, false);

			canvas.drawBitmap(bm,level.player.getX(),level.player.getY(), null);


		}
		//

		//Draw NPCs
		try{
			for ( NPC npc : level.npc){
				if(npc.draw == true){
					if(npc.getDirection()==Level.LEFT){
						canvas.drawBitmap(npc.animation.main,npc.getX()-level.cameraX,npc.getY(), null);

					}else{
						Matrix m = new Matrix();
						m.preScale(-1.0f, 1.0f);
						Bitmap bm = Bitmap.createBitmap(npc.animation.main,0,0, npc.width, npc.height, m, false);

						canvas.drawBitmap(bm,npc.getX()-level.cameraX,npc.getY(), null);

					}
				}
			}
		}catch( ConcurrentModificationException e ){

		}catch( NullPointerException e ){

		}
		//

		//Draw Weapon
		int xMod = level.player.weapon.xPosMod;
		int yMod = level.player.weapon.yPosMod;

		if(level.player.getDirection()=="Right"){
			if(!level.player.weapon.name.equals(Weapon.FIST))
				canvas.drawBitmap(level.player.weapon.animation.main,level.player.weapon.x - xMod,level.player.weapon.y + yMod, null);
			level.player.weapon.flip = false;
		}else{
			Matrix m = new Matrix();
			m.preScale(-1.0f, 1.0f);
			Bitmap bm = Bitmap.createBitmap(level.player.weapon.animation.main,0,0, level.player.weapon.width, level.player.weapon.height, m, false);
			if(!level.player.weapon.name.equals(Weapon.FIST))
				canvas.drawBitmap(bm,level.player.weapon.x - xMod,level.player.weapon.y + yMod, null);

			level.player.weapon.flip = true;

		}

		//

		//Draw Water
		for (int i = 0; i < level.block.size(); i++) {
			if(isOnScreen(level.block.get(i))==true){
				if(level.block.get(i).animation.isAvailable){
					if(level.block.get(i).animation.playing == false){
						if(!level.block.get(i).getType().equals(level.INVENTORY_ITEM)){
							level.block.get(i).animation.start(0, 0);
						}
						//level.toast("Started:");
					}
					if(level.block.get(i).getType().equals(level.WATER)){
						canvas.drawBitmap(level.block.get(i).animation.main, level.block.get(i).getX()-level.cameraX,level.block.get(i).getY(), null);

					}
				}
			}else{
				level.block.get(i).animation.stop();
				//level.toast("Stoped:");
			}
		}

		//

		//Draw Effects
		try{
			for (ScreenObject effect : level.screenEffects) {
				if(effect.animation.playing == true){
					canvas.drawBitmap(effect.animation.main, effect.getX() - level.cameraX,effect.getY(), null);
				}else{
					level.screenEffects.remove(effect);
				}
			}
		} catch( ConcurrentModificationException e ){

		}

		//

		//Draw darkness

		Paint dark = new Paint();
		dark.setColor(Color.BLACK);
		dark.setAntiAlias(true);

		alpha = level.getTime() - 12;
		alpha = (double) Math.pow(alpha, 2);
		dark.setAlpha((int) alpha);

		//canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(), darkColor);
		canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(), dark);
		//canvas.drawText("Alpha: " + alpha, 10, 100, paint);
		//canvas.drawText("AlphaColor: " + cAlpha, 10, 140, paint);

		//

		//Draw sun glow
		Paint sunp = new Paint();
		sunp.setColor(Color.WHITE);
		sunp.setAntiAlias(true);
		double sunAlpha = level.getTime() - 12;
		sunAlpha = (double) Math.pow(sunAlpha, 2);
		sunp.setAlpha((int) sunAlpha);
		canvas.drawBitmap(glow, sun.x, sun.y, sunp);
		//canvas.drawText(sun.toString(), 10, 180, paint);


		//

		//Draw fire glow
		Paint glowp = new Paint();
		glowp.setAlpha((int) (glowp.getAlpha()/1.5f));

		if(level.player.getDirection()=="Right"){

			if(level.player.weapon.name.equals(Weapon.TORCH))
				canvas.drawBitmap(Bitmap.createScaledBitmap(glow,(level.player.weapon.animation.main.getWidth()*4),(level.player.weapon.animation.main.getHeight()*4),true),level.player.bodyCollision().right - xMod - (level.player.weapon.animation.main.getWidth()*2),level.player.getY() + yMod - (level.player.weapon.animation.main.getHeight()*2), glowp);
		}else{
			if(level.player.weapon.name.equals(Weapon.TORCH))
				canvas.drawBitmap(Bitmap.createScaledBitmap(glow,level.player.weapon.animation.main.getWidth()*4,level.player.weapon.animation.main.getHeight()*4,true),level.player.bodyCollision().right - xMod - (level.player.weapon.animation.main.getWidth()*2) - (level.player.size/2),level.player.getY() + yMod - (level.player.weapon.animation.main.getHeight()*2), glowp);
		}
		//

		//Dev Mode
		if(level.DEV_MODE == true){
			bodyCol = level.player.bodyCollision();
			bodyColX2 = new Rect(bodyCol.left - bodyCol.width(),bodyCol.top - bodyCol.height(),bodyCol.right+bodyCol.width(),bodyCol.bottom+bodyCol.height());
			canvas.drawRect(bodyColX2, paint); 
			canvas.drawRect(level.blsCol2, paint); 
			canvas.drawRect(level.player.rightCollision(), paint);
			canvas.drawRect(level.player.leftCollision(), paint);
			canvas.drawRect(level.player.feetCollision(), paint);
			canvas.drawRect(level.player.weapon.Collision(), paint);
			for(NPC npcs : level.npc){
				Rect treeCol = npcs.Collision();
				int left = treeCol.left - level.cameraX;
				int right = treeCol.right - level.cameraX;
				int top = treeCol.top;
				int bottom = treeCol.bottom;
				treeCol = new Rect(left,top,right,bottom);
				canvas.drawRect(treeCol, paint);

			}

			for (int i = 0; i < level.tree.size(); i++) {
				Rect treeCol = level.tree.get(i).Collision();
				int left = treeCol.left - level.cameraX;
				int right = treeCol.right - level.cameraX;
				int top = treeCol.top;
				int bottom = treeCol.bottom;
				treeCol = new Rect(left,top,right,bottom);
				canvas.drawRect(treeCol, paint); 
			}
			for (int i = 0; i < level.stone.size(); i++) {
				Rect stoneCol = level.stone.get(i).Collision();
				int left = stoneCol.left - level.cameraX;
				int right = stoneCol.right - level.cameraX;
				int top = stoneCol.top;
				int bottom = stoneCol.bottom;
				stoneCol = new Rect(left,top,right,bottom);
				canvas.drawRect(stoneCol, paint); 
			}
			canvas.drawText(level.blstring, 40, 50, paint);
			canvas.drawText(level.distancetext + "",50,65,paint);
			canvas.drawText(level.player.movingDir,50,80,paint);

		}

		//Draw HUD
		String health = "Health: "+level.player.health;
		String hunger = "Hunger: "+level.player.hunger;
		String thirst = "Thirst: "+level.player.thirst;
		String time = "Time: "+level.getTimeString();
		String fpsString = "FPS: "+lastfps;

		IntSize tmp = level.getPercentSize(PERCENT_BAR_W, PERCENT_BAR_H);
		float healthP = 0;
		if( level.selectedItem.health > 0 ){
			healthP = (tmp.width / (100.0f/level.selectedItem.health));

		}
		IntSize healthLoc = level.getPercentSize(25,5);
		IntSize hungerLoc = level.getPercentSize(50,5);
		IntSize thirstLoc = level.getPercentSize(75,5);
		IntSize timeLoc = level.getPercentSize(5,5);
		IntSize fpsLoc = level.getPercentSize(75,10);

		canvas.drawText(time, timeLoc.width, timeLoc.height, paint);
		canvas.drawText(health, healthLoc.width, healthLoc.height, paint);
		canvas.drawText(hunger, hungerLoc.width, hungerLoc.height, paint);
		canvas.drawText(thirst, thirstLoc.width, thirstLoc.height, paint);
		canvas.drawText(fpsString, fpsLoc.width, fpsLoc.height, paint);

		if( level.toastLog.size() > 0 ){
			try{
				IntSize loc = level.getPercentSize(50, 70);



				canvas.drawText(level.toastLog.get(0), loc.width - paint.measureText(level.toastLog.get(0))/2, loc.height - paint.getTextSize(), paint);
			}catch( IndexOutOfBoundsException e ){

			}

		}
		//

		//Draw buttons

		canvas.drawBitmap(level.cursorBG,level.cursorX,level.cursorY, cursorP);
		if(level.buttonAreas.isEmpty() == false){
			try{
				canvas.drawBitmap(attackHud,level.buttonAreas.get(attackint).x,level.buttonAreas.get(attackint).y, cursorP);
			}catch( IndexOutOfBoundsException e ){
				Log.e("Error", e.getMessage());
			}
		}
		//
		fps++;
		if(runfps){
			runfps=false;
			hd.postDelayed(clearfps, 1000);
		}
	}

	public void DrawCabin(Canvas canvas){



		level.cabin_bg  = Bitmap.createScaledBitmap(level.cabin_bg,canvas.getWidth() , canvas.getHeight(),true);
		canvas.drawBitmap(level.cabin_bg,0,0, null);

	}

	public void DrawCrafting(Canvas canvas){


		Paint p = new Paint();

		level.inventory_bg  = Bitmap.createScaledBitmap(level.inventory_bg,canvas.getWidth() , canvas.getHeight(),true);
		canvas.drawBitmap(level.inventory_bg,0,0, p);

	}

	public void DrawInventory(Canvas canvas){

		int y = level.getPercentSize(5.0f);
		int rightBoxX = canvas.getWidth() - canvas.getWidth()/3;
		Point imageLoc = new Point(rightBoxX + level.getPercentSize(10),level.getPercentSize(5.0f));
		Point nameLoc = new Point(rightBoxX,level.getPercentSize(35.0f));
		Point amountLoc = new Point(rightBoxX + canvas.getWidth()/8,level.getPercentSize(35.0f));
		Point typeLoc = new Point(rightBoxX,level.getPercentSize(50.0f));
		Point displayNameLoc = new Point(rightBoxX,level.getPercentSize(65.0f));
		Point descriptionLoc = new Point(rightBoxX,level.getPercentSize(75.0f));

		//Draw inventory background
		Paint p = new Paint();
		//p.setAlpha(100);
		level.inventory_bg  = Bitmap.createScaledBitmap(level.inventory_bg,canvas.getWidth() , canvas.getHeight(),true);
		canvas.drawBitmap(level.inventory_bg,0,0, p);
		canvas.drawRect(level.box, paint);


		for(Rect buttons : level.buttonbox){
			canvas.drawRect(buttons, paint);

		}
		for(InventoryItem items: level.player.inventory){
			try{
				int textx = level.buttonAreas.get(level.player.inventory.indexOf(items)).x;
				int texty = level.buttonAreas.get(level.player.inventory.indexOf(items)).y;
				int textw = level.buttonAreas.get(level.player.inventory.indexOf(items)).width;
				int texth = level.buttonAreas.get(level.player.inventory.indexOf(items)).height;
				textx += textw/10;
				texty += texth/2;
				IntSize adj = level.getPercentSize(2, 2);
				canvas.drawBitmap(Bitmap.createScaledBitmap(items.image, texth - (int)adj.width, texth - (int)adj.width,true), textx - (textw/10) + (int)adj.width,texty - (texth/2) + ((int)adj.height/2), null);
				canvas.drawText(items.displayName + ": " + items.getAmount(), textx + (int)adj.width,texty + (texth/2) - (paint.getTextSize()), paint);
				//canvas.drawText(level.inventory., 55, 300, paint);
				y += texth;
			}catch( IndexOutOfBoundsException e ){

			}
		}
		ButtonArea equip = level.buttonAreas.get(level.buttonAreas.size()-2);
		canvas.drawText("Equip", equip.x + (equip.width/2) - (paint.measureText("Equip")/2),equip.y + (equip.height/2) + (paint.getTextSize()/2), paint);

		ButtonArea toss = level.buttonAreas.get(level.buttonAreas.size()-1);
		canvas.drawText("Toss", toss.x + (toss.width/2) - (paint.measureText("Toss")/2),toss.y + (toss.height/2) + (paint.getTextSize()/2), paint);

		if(level.selectedItem != null){

			int iWidth = level.getPercentSize(10);

			canvas.drawBitmap(Bitmap.createScaledBitmap(level.selectedItem.image, iWidth, iWidth,true), imageLoc.x, imageLoc.y, null);
			canvas.drawText(level.selectedItem.getName(), nameLoc.x, nameLoc.y, paint);
			canvas.drawText(level.selectedItem.displayName, displayNameLoc.x, displayNameLoc.y, paint);
			canvas.drawText(level.selectedItem.type, typeLoc.x, typeLoc.y, paint);

			if(level.selectedItem.type == Weapon.WEAPON){

				canvas.drawText("Health: " + level.selectedItem.health, amountLoc.x, amountLoc.y - level.getPercentSize(1.0f), paint);
				canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.percentbarback), level.getPercentSize(PERCENT_BAR_W), level.getPercentSize(PERCENT_BAR_H), true),amountLoc.x, amountLoc.y, null);

				IntSize tmp = level.getPercentSize(PERCENT_BAR_W, PERCENT_BAR_H);
				float healthP = 0;
				if( level.selectedItem.health > 0 ){
					healthP = (tmp.width / (100.0f/level.selectedItem.health));

				}else{
					healthP = 1;
				}
				try{
					canvas.drawBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.percentbar), (int) healthP, (int) tmp.height, true),amountLoc.x, amountLoc.y, null);
				}catch( IllegalArgumentException e ){

				}
			}else{

				canvas.drawText("Amount: " + level.selectedItem.getAmount(), amountLoc.x, amountLoc.y, paint);
			}

		}
		//
	}

	public void DrawJournal(Canvas canvas){



		level.journal_bg  = Bitmap.createScaledBitmap(level.journal_bg,canvas.getWidth() , canvas.getHeight(),true);
		canvas.drawBitmap(level.journal_bg,0,0, null);

	}
	public void DrawLoading(Canvas canvas){


		canvas.drawColor(Color.BLACK);
		canvas.drawText("LOADING...", canvas.getWidth()/2 - (25), canvas.getHeight(), paint);

	}

	public void DrawPause(Canvas canvas){

		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setTextSize(24);
		p.setStrokeWidth(1);
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		level.pause_bg  = Bitmap.createScaledBitmap(level.pause_bg,canvas.getWidth()  - 100, canvas.getHeight() - 100,true);
		canvas.drawBitmap(level.pause_bg,50,50, p);
		canvas.drawText("Pause", canvas.getWidth()/2 - 25, 75, p);
		//screen_width/2 - (screen_width/4),screen_height/2 + (screen_height/4)
		int bX = canvas.getWidth()/2 - (canvas.getWidth()/4);
		int bY = canvas.getHeight()/2 + (canvas.getHeight()/8);
		canvas.drawRect(new Rect(bX, bY, bX + level.BUTTON_SIZE*4, bY + level.BUTTON_SIZE),paint);
		canvas.drawText("Save and Quit", canvas.getWidth()/2 - 65, bY + 55, p);

	}
	public boolean isOnScreen(Blocks bl){

		boolean onScreen = true;
		if((bl.getX() - level.cameraX) < (0 - bl.getSize()) || (bl.getX() - level.cameraX) > (bl.getSize() + level.screen_width)){
			onScreen = false;
		}

		return onScreen;

	}

	public void toast(String message){
		final String mes = message;
		handler.post(new Runnable(){
			public void run(){
				Toast.makeText(getContext(), mes, Toast.LENGTH_LONG).show();
			}
		});

	}
}
