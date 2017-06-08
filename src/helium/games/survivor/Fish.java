package helium.games.survivor;

import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.speech.SpeechRecognizer;

public class Fish extends NPC {

	public static final int P_HEIGHT = 5; //%
	public static final int P_WIDTH = 5; //%
	public static final int JUMP_HEIGHT_P = 1;
	public static final int MAX_HEALTH = 15;

	public int pWidth = P_WIDTH;
	public int pHeight = P_HEIGHT;

	public int moveChance = 30;

	public Fish(Resources res, Level level, String variety) {

		super(variety, NPC.ANIMAL_FISH + " ", false, 0, 0, Level.LEFT, NPC.ACTION_IDLE, res,
				level);
		

		IntSize size = level.getPercentSize(pWidth, pHeight);
		int swidth =  (int)(size.width);
		int sheight =  (int)(size.height);
		
		this.value = 10;
		 if(this.name == NPC.ANIMAL_FISH_SALMON){
			 
			 swidth *=10;
			 sheight += (sheight/2);
			 this.value = 20;
			 
		 }
		 
		this.width = sheight;
		this.height = sheight;
		Bitmap fish = BitmapFactory.decodeResource(res, R.drawable.fish);
		fish = Bitmap.createScaledBitmap(fish, swidth , sheight, true);
		this.image_idle = fish;
		this.health = MAX_HEALTH;
		this.maxHealth = MAX_HEALTH;
		blockResistant = false;
		gravityResistant = true;

		this.animation = new Animation(fish, x, y, this.width, this.height);

		if(this.name == NPC.ANIMAL_FISH_BLUEGILL){
			setupBlueGill( res, this.width, this.height );
		}else if(this.name == NPC.ANIMAL_FISH_SALMON){
			setupSalmon( res, swidth, sheight );
		}

		handler.post(active);

	}

	public void setupBlueGill( Resources res, int width, int height ){

		this.displayName = ANIMAL_FISH_BLUEGILL + " Meat";
		this.name = ANIMAL_FISH_BLUEGILL;
		this.health = maxHealth;
		speed = 4;
		Bitmap fish = BitmapFactory.decodeResource(res, R.drawable.fish);
		fish = Bitmap.createScaledBitmap(fish, width , height, true);
		this.animation = new Animation(fish, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.fish1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.fish2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);


		Bitmap images[] = {fish,image1,fish,image2};

		int durations[] = {200,200,200,200};

		this.animation.createAnimation(images, durations, true, 0);
		this.animation.start(0, 0);

	}

	public void setupSalmon( Resources res, int width, int height ){

		this.displayName = ANIMAL_FISH_SALMON + " Meat";
		this.name = ANIMAL_FISH_SALMON;
		this.health = maxHealth*2;
		speed = 2;
		Bitmap fish = BitmapFactory.decodeResource(res, R.drawable.salmon);
		fish = Bitmap.createScaledBitmap(fish, width , height, true);
		this.animation = new Animation(fish, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.salmon1);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.salmon2);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);


		Bitmap images[] = {fish,image1,fish,image2};

		int durations[] = {200,200,200,200};

		this.animation.createAnimation(images, durations, true, 0);
		this.animation.start(0, 0);

	}

	private final Runnable active = new Runnable() {
		@Override
		public void run() {

			Random r = new Random();
			if(action == ACTION_IDLE){

				if( r.nextInt(moveChance) == 1){				//Turn

					if(getDirection() == Level.LEFT){
						setDirection(Level.RIGHT);
					}else{
						setDirection(Level.LEFT);
					}

				}
				
				if( r.nextInt(moveChance) == 1){				//WANDER
					action = ACTION_WANDERING;
					
				}
				
			}else if(action == ACTION_WANDERING){

				if( r.nextInt(moveChance) == 1){				//Turn

					if(getDirection() == Level.LEFT){
						setDirection(Level.RIGHT);
					}else{
						setDirection(Level.LEFT);
					}

				}
				
				if(getDirection() == Level.LEFT){
					
						if(canMoveLeft == true){
							moveX(-(1));
						}
					
				}

				if(getDirection() == Level.RIGHT){
					
						if(canMoveRight == true){
							moveX((1));
						}
					
				}

				if( r.nextInt(moveChance) == 1){				//IDLE
					action = ACTION_IDLE;
					
				}
				
			}else if(action == ACTION_FLEEING){

			}else if(action == ACTION_ATTACKING){

			}
			if( health < 0 ){
				die();
			}
			if(getY() < (0 - height) || getX() - level.cameraX < (level.player.getX() - level.screen_width) || getX() - level.cameraX > (level.player.getX() + level.screen_width) || getY() > (level.screen_height + height)){
				die();
			}
			try{
				handler.postDelayed(active,100);
			}catch( NullPointerException e ){

			}
		}
	};

	public void setAction( String activity ){
		this.action = activity;
	}

}
