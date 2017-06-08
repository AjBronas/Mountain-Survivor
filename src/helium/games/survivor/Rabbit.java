package helium.games.survivor;

import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class Rabbit extends NPC {

	public static final int P_HEIGHT = 7; //%
	public static final int P_WIDTH = 7; //%
	public static final int JUMP_HEIGHT_P = 5;
	public static final int MAX_HEALTH = 30;

	public int pWidth = P_WIDTH;
	public int pHeight = P_HEIGHT;

	public int speed = 3;

	public Rabbit(Resources res, Level level) {

		super(NPC.ANIMAL_RABBIT, NPC.ANIMAL_RABBIT + " ", false, 0, 0, Level.LEFT, NPC.ACTION_IDLE, res,
				level);


		IntSize size = level.getPercentSize(pWidth, pHeight);
		int swidth =  (int)(size.width);
		int sheight =  (int)(size.height);
		this.width = sheight;
		this.height = sheight;
		Bitmap rabbit = BitmapFactory.decodeResource(res, R.drawable.bunny1);
		rabbit = Bitmap.createScaledBitmap(rabbit, swidth , sheight, true);
		this.image_idle = rabbit;
		this.health = MAX_HEALTH;
		this.maxHealth = MAX_HEALTH;
		this.blockResistant = false;
		this.value = 10;
		this.animation = new Animation( res );
		if(this.name == NPC.ANIMAL_RABBIT){
			setupBunny( res, width, height );
		}

		handler.post(active);

	}

	public void setupBunny( Resources res, int width, int height ){

		this.displayName = ANIMAL_RABBIT + " Meat";
		Bitmap rabbit = BitmapFactory.decodeResource(res, R.drawable.bunny1);
		rabbit = Bitmap.createScaledBitmap(rabbit, width , height, true);
		this.animation = new Animation(rabbit, x, y, width, height);

		Bitmap image1 = BitmapFactory.decodeResource(res, R.drawable.bunny2);
		image1 = Bitmap.createScaledBitmap(image1, width, height, true);

		Bitmap image2 = BitmapFactory.decodeResource(res, R.drawable.bunny3);
		image2 = Bitmap.createScaledBitmap(image2, width, height, true);

		Bitmap image3 = BitmapFactory.decodeResource(res, R.drawable.bunny4);
		image3 = Bitmap.createScaledBitmap(image3, width, height, true);

		Bitmap image4 = BitmapFactory.decodeResource(res, R.drawable.bunny1);
		image4 = Bitmap.createScaledBitmap(image4, width, height, true);

		Bitmap images[] = {rabbit,image1,image2,image3,image4};

		int durations[] = {0,100,100,250,100};

		this.animation.createAnimation(images, durations, false, 0);

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

				if( r.nextInt(100) == 1){				//Jump
					if(animation.playing == false){
						animation.start(1, animation.images.length);
						if(jumping == false){

							jump(jumpHeight);

						}
					}
				}

			}else if(action == ACTION_WANDERING){


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
				
				if( r.nextInt(100) == 1){				//Jump
					if(animation.playing == false){
						animation.start(1, animation.images.length);
						if(jumping == false){

							jump(jumpHeight);

						}
					}
				}

			}else if(action == ACTION_FLEEING){

			}else if(action == ACTION_ATTACKING){

			}

			if(jumping == true){
				if(y > jumpSpot.y - jumpHeight){
					if(getDirection() == Level.LEFT){
						velocity = new Point(-(jumpSpeed),-(jumpSpeed));
					}else{
						velocity = new Point(jumpSpeed,-(jumpSpeed));
					}
					moveX(velocity.x);
					if(gravityResistant == true){
						moveY(velocity.y);
					}
				}else{
					gravityResistant = false;

				}

				if(getDirection() == Level.LEFT){
					if(x < jumpSpot.x - (jumpHeight*1.5)){
						jumping = false;
					}
				}else{
					if(x > jumpSpot.x + (jumpHeight*1.5)){
						jumping = false;
					}
				}


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

	public void setAction( String activity ){
		this.action = activity;
	}

}
