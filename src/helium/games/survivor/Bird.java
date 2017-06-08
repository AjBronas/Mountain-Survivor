package helium.games.survivor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;

public class Bird extends NPC {

	public static final int P_HEIGHT = 5; //%
	public static final int P_WIDTH = 5; //%
	public static final int MAX_HEALTH = 25;

	public int pWidth = P_WIDTH;
	public int pHeight = P_HEIGHT;

	public int speed = 3;
	public int treeId = 0;
	public List<Tree> onTree = new ArrayList<Tree>();
	public Bird(Resources res, Level level) {

		super(NPC.ANIMAL_BIRD_DOVE, NPC.ANIMAL_BIRD, false, 0, 0, Level.LEFT, NPC.ACTION_IDLE, res,
				level);

		Random r = new Random();
		gravityResistant = true;
		if( r.nextInt(4) == 0 ){
			gravityResistant = false;
		}
		IntSize size = level.getPercentSize(pWidth, pHeight);
		int swidth =  (int)(size.width);
		int sheight =  (int)(size.height);
		this.width = sheight;
		this.height = sheight;
		Bitmap dove = BitmapFactory.decodeResource(res, R.drawable.bird);
		dove = Bitmap.createScaledBitmap(dove, swidth , sheight, true);
		this.image_idle = dove;
		this.health = MAX_HEALTH;
		this.maxHealth = MAX_HEALTH;
		this.blockResistant = true;
		this.value = 10;
		this.animation = new Animation( res );
		if(this.name == NPC.ANIMAL_BIRD_DOVE){
			setupDove( res, width, height );
		}else if(this.type == NPC.ANIMAL_BIRD_CROW){
			setupCrow( res, width, height );
		}

		handler.post(active);

	}

	@Override
	public void spawn( int x, int y ){

		super.spawn(x, y);
		Rect npcCol = Collision();
		npcCol = new Rect(npcCol.left - level.cameraX +(width/2),npcCol.top+(height/2),npcCol.right - level.cameraX-(width/2),npcCol.bottom-(height/2));
		List<ScreenObject> objarea = level.findObjectsinArea(npcCol,
				false);

		for(ScreenObject tree : objarea){
			if(tree instanceof Tree){
				onTree.add((Tree) tree);
			}
		}
	}

	@Override
	public void spawn( Point point ){

		super.spawn(point);
		Rect npcCol = Collision();
		npcCol = new Rect(npcCol.left - level.cameraX +(width/2),npcCol.top+(height/2),npcCol.right - level.cameraX-(width/2),npcCol.bottom-(height/2));
		List<ScreenObject> objarea = level.findObjectsinArea(npcCol,false);

		for(ScreenObject tree : objarea){
			if(tree instanceof Tree){
				onTree.add((Tree) tree);
			}
		}
	}

	
	public void setupDove( Resources res, int width, int height ){

		this.displayName = ANIMAL_BIRD_DOVE + " Meat";
		Bitmap dove = BitmapFactory.decodeResource(res, R.drawable.bird);
		dove = Bitmap.createScaledBitmap(dove, width , height, true);
		this.animation = new Animation(dove, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.bird1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.bird2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image3 = BitmapFactory.decodeResource(res, R.drawable.bird3);
		image3 = Bitmap.createScaledBitmap(image3, width, height, true);

		Bitmap image4 = BitmapFactory.decodeResource(res, R.drawable.bird4);
		image4 = Bitmap.createScaledBitmap(image4, width, height, true);

		Bitmap images[] = {dove,image1,image2,image3,image4};

		int durations[] = {0,50,50,50,50};

		this.animation.createAnimation(images, durations, false, 0);

	}

	public void setupCrow( Resources res, int width, int height ){

		this.displayName = ANIMAL_BIRD_CROW + " Meat";
		Bitmap dove = BitmapFactory.decodeResource(res, R.drawable.bird);
		dove = Bitmap.createScaledBitmap(dove, width , height, true);
		this.animation = new Animation(dove, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.bird1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.bird2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image3 = BitmapFactory.decodeResource(res, R.drawable.bird3);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image4 = BitmapFactory.decodeResource(res, R.drawable.bird4);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap images[] = {dove,image1,image2,image3,image4};

		int durations[] = {0,50,50,50,50};

		this.animation.createAnimation(images, durations, true, 0);

	}

	private final Runnable active = new Runnable() {
		@Override
		public void run() {

			Random r = new Random();
			if(action == ACTION_IDLE){


				if( r.nextInt(30) == 1){				//Turn
					if(animation.playing == false){
						if( jumping != true ){
							if(getDirection() == Level.LEFT){
								setDirection(Level.RIGHT);
							}else{
								setDirection(Level.LEFT);
							}
						}
					}
				}

				if( r.nextInt(100) == 1){

					if(animation.playing == false){
						animation.start(1, 5);
					
					}
				}

				if( r.nextInt(150) == 1){
					setFlying();
				}

				for( Tree tree : onTree ){
					if( tree.health < tree.maxHealth ){
						setFlying();
					}
				}
			}else if(action == ACTION_WANDERING){


			}else if(action == ACTION_FLEEING){

				if(animation.playing == false){
					animation.start(0, 0);
					flying = true;
				}

				if(flying == true){
					moveX(velocity.x);
					moveY(velocity.y);
				}


			}else if(action == ACTION_ATTACKING){

			}



			if( health < 0 ){
				die();
			}
			if(getY() < (0 - height) || getX() - level.cameraX < (level.player.getX() - level.screen_width) || getX() - level.cameraX > (level.player.getX() + level.screen_width)){
				die();
			}
			try{
				handler.postDelayed(active,100);
			}catch( NullPointerException e ){

			}
		}
	};

	public void setFlying(){

		this.flying = true;
		action = NPC.ACTION_FLEEING;
		Random r = new Random();
		int x = 1;
		int y = r.nextInt(5);
		y = y*(-1);
		if(getDirection() == Level.LEFT){
			x = x*(-1);
		}
		y = y*2;
		x = x*4;
		velocity = new Point(x,y);

		animation.images[0] = animation.images[1];
	}

	public void setAction( String activity ){
		this.action = activity;
	}

}
