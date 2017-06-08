/** 
 * NPCs are characters ingame. They extend from ScreenObject
 * for location/size/collision.
 * 
 * NPCs are weak to SWORDS.
 * 
 * NPCs can buy and sell items.
 * 
 * They are normally friendly but can become hostile if provoked.
 */
package helium.games.survivor;

import java.util.List;
import java.util.Random;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;

public class NPC extends ScreenObject{

	public final static String ACTION_IDLE = "IDLE";
	public final static String ACTION_ATTACKING = "ATTACKING";
	public final static String ACTION_WANDERING = "WANDERING";
	public final static String ACTION_FLEEING = "FLEEING";

	public final static String ANIMAL = "Animal";
	public final static String ANIMAL_BIRD = "Bird";
	public final static String ANIMAL_BIRD_DOVE = "Dove";
	public final static String ANIMAL_BIRD_CROW = "Crow";
	public final static String ANIMAL_RABBIT = "Rabbit";
	public final static String ANIMAL_FISH = "Fish";
	public final static String ANIMAL_FISH_BLUEGILL = "Blue gill";
	public final static String ANIMAL_FISH_SALMON = "Salmon";
	public final static String ANIMAL_DEER = "Deer";
	public final static String ANIMAL_BEAR = "Bear";

	public static final int JUMP_HEIGHT_P = 5;


	public boolean isDead = false;
	public boolean human = false;
	public int x = 0;
	public int y = 0;
	public int size = 1;
	public Bitmap image_idle;
	public Bitmap image_dead;
	public String facing = "Left";
	public int value = 1;
	public boolean isOnGround = false;
	public String action = ACTION_IDLE;
	public boolean draw = false;
	public boolean canMoveRight = true;
	public boolean canMoveLeft = true;
	public Blocks lastTouchedBlock;
	public boolean blockResistant = false;
	public boolean gravityResistant = false;
	public boolean flying = false;
	public Point velocity = new Point();
	public boolean jumping = false;
	public Point jumpSpot = new Point();
	public int jumpHeight = JUMP_HEIGHT_P;
	public int jumpSpeed = 7;
	public int speed = 3;
	Handler handler = new Handler();

	public NPC(String name, String type, Boolean human, int x, int y,
			String facing, String mode, Resources res, Level level ){

		super(res, level);
		super.optionString = ANIMAL;
		this.name = name;
		this.type = type;
		this.human = human;
		this.x = x;
		this.y = y;
		this.facing = facing;
		this.action = mode;
		this.jumpHeight = level.getPercentSize( JUMP_HEIGHT_P );

	}

	public void spawn( int x, int y ){

		setX(x);
		setY(y);
		draw = true;
		handler.post(checkCol);

	}

	public void spawn( Point point ){

		setX(point.x);
		setY(point.y);
		draw = true;
		handler.post(checkCol);

	}

	public void setX(int x){

		this.x = x;

	}

	public void setY(int y){

		this.y = y;

	}

	public void setDirection(String facing){

		this.facing = facing;

	}

	public int getX(){

		return this.x;

	}

	public int getY(){

		return this.y;

	}

	public String getDirection(){

		return this.facing;

	}

	public void moveX(int amount){

		this.x+=amount;

	}

	public void moveY(int amount){

		this.y+=amount;

	}

	public void jump(int height){

		this.jumping = true;
		this.jumpSpot = new Point(x,y);
		this.gravityResistant = true;

	}

	private Runnable checkCol = new Runnable() {  

		public void run() {  
			Rect npcCol = Collision();
			npcCol = new Rect(npcCol.left - level.cameraX,npcCol.top,npcCol.right - level.cameraX,npcCol.bottom);
			List<Blocks> blocksarea = level.findBlocksInArea(npcCol);
			int solidblocks = 0;
			for(Blocks bls : blocksarea){

				Rect Col =  bls.Collision();
				//Left				Top		Right			Bottom
				Col = new Rect(Col.left -level.cameraX,Col.top,Col.right -level.cameraX,Col.bottom);
				if(Rect.intersects(Col, npcCol)){



					if( bls.isSolid == true ){
						solidblocks++;

					}

					if(bls.getType().equals(level.WATER)){
						if(!type.contains(ANIMAL_FISH)){
							health--;
						}

					}



				}
				
				if(blockResistant == false){
					checkHorizontalCol(bls,solidblocks, blocksarea.size());
					
				}

			}

			if(blocksarea.size()>0){
				if(solidblocks>0){

					isOnGround = true;

				}else{
					isOnGround = false;
					if((getY() + height) >= level.screen_height){
						isOnGround = true;
					}
				}

			}
			//if(player.isOnGround==false){
			handler.post(checkCol);
			//}
		}     

	};
	
	public void checkHorizontalCol(Blocks bls,int currentint, int intcount){	//follows same idea for solidblock count

		String object1Direction = Level.LEFT;
		
		Rect npcCol = Collision();
		npcCol = new Rect(npcCol.left - level.cameraX,npcCol.top,npcCol.right - level.cameraX,npcCol.bottom);
		
	
		if(getDistanceX(npcCol,bls.Collision()) ==0){
			
			if(bls.getY()+(bls.getSize()/2) < npcCol.bottom){
				if(bls.isSolid==true){
					lastTouchedBlock = bls;
					
					if(npcCol.centerX() > bls.Collision().centerX() - level.cameraX){
						
						if(getDirection() == Level.LEFT){
							
							canMoveLeft = false;
							
						}
					}else{

						if(getDirection() ==Level.RIGHT){
							
							canMoveRight = false;
							
						}
					}
				}
			}
		}else{
			if(lastTouchedBlock != null){
	
				if(getDistanceX(npcCol,lastTouchedBlock.Collision()) > 1){
					if(canMoveLeft==false){
						canMoveLeft= true;
					}
					if(canMoveRight==false){
						canMoveRight= true;
					}
				}
			}

		}



	}

	public int getDistanceX(Rect object1, Rect object2){	//ob1 should be player, ob2 should be other

		int distance = 5;
		String object1Direction = Level.LEFT;

		if(object1.centerX() > object2.centerX()- level.cameraX){

			object1Direction = Level.RIGHT;

		}

		if(getDirection() == Level.LEFT){	//if object1 is to the right of object2
			if(object2.centerY() < (object1.bottom-5))
				distance = (object1.centerX() - (object1.width()/2)) - ((object2.centerX()- level.cameraX) + (object2.width()/2));

		}else if(getDirection() == Level.RIGHT){					//if object1 is to the left of object2
			if(object2.centerY() < (object1.bottom-5))
				distance = (object1.centerX() + (object1.width()/2)) - ((object2.centerX()- level.cameraX) - (object2.width()/2));

		}
		if(distance < 0){
			distance = distance *-1;
		}
		return distance;

	}

	
	@Override
	public void hit( Weapon weapon, int damage ){

		if(weapon != null){
			float dmg = weapon.damage;
			
			level.effects.width = width;
			level.effects.height = height;
			ScreenObject blood = level.effects.blood();

			blood.x = x;
			blood.y = y + height - (height/2);
			blood.animation.start(0, 0);
			level.screenEffects.add(blood);
			if(weapon.name == Weapon.SWORD)
				dmg *= 2;
			this.health -= dmg;

		}else{

			this.health -= damage;
		}

		if(this.health <= 0){

			addToInventory();
			die();
		}




	}

	public void die(){

		this.health = 0;
		this.isDead = true;
		handler.removeMessages(0);
		handler.removeCallbacks(checkCol);
		handler = null;
		animation = null;
		level.npc.remove(this);
	}

	public void addToInventory(){

		InventoryItem npc = new InventoryItem( name, displayName, type, 1, animation.images[0], false, res, level);
		npc.optionString = optionString;
		npc.value = value;
		level.player.inventory.add( npc );

	}
	public boolean isOnScreen(int playerx, int playery,int canvasw, int canvash){

		boolean isonscreen = false;
		if(this.getX()>=(0-this.size)&this.getY()>=(0-this.size)){

			if(this.getX()<=(Level.screen_width+this.size)&this.getY()>=(Level.screen_height+this.size)){

				isonscreen = true;

			}

		}


		return isonscreen;

	}

	@Override
	public Rect Collision(){

		Rect r = new Rect(this.x,this.y,this.x+this.width,this.y+this.height);
		return r;

	}

}
