package edu.arizona.gui {
	import edu.arizona.SoundManager;
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.MouseEvent;

	public class Button3State extends Sprite {
		
		protected var state1:DisplayObject;
		protected var state2:DisplayObject;
		protected var state3:DisplayObject;
		private var curState:DisplayObject;
		private var soundMan:SoundManager = new SoundManager();
		
		public function Button3State(stateUp:DisplayObject, stateOver:DisplayObject, stateDown:DisplayObject) {
			state1 = stateUp;
			state2 = stateOver;
			state3 = stateDown;
			
			curState = state1;
			state1.visible = true;
			state2.visible = false;
			state3.visible = false;
			
			addChild(state1);
			addChild(state2);
			addChild(state3);
			
			this.addEventListener(MouseEvent.MOUSE_DOWN, onDown, false, 0, true);
			this.addEventListener(MouseEvent.MOUSE_UP, onUp, false, 0, true);
			this.addEventListener(MouseEvent.MOUSE_OVER, onOver, false, 0, true);
			this.addEventListener(MouseEvent.MOUSE_OUT, onOut, false, 0, true);
		}
		
		protected function onDown(evt:MouseEvent):void{
			soundMan.button_click_play();
			changeState(state3);
		}
		
		protected function onUp(evt:MouseEvent):void{
			changeState(state2);
		}
		
		protected function onOver(evt:MouseEvent):void {
			soundMan.hover_play();
			changeState(state2);
		}
		
		protected function onOut(evt:MouseEvent):void{
			changeState(state1);
		}
		
		protected function changeState(newState:DisplayObject):void{
			curState.visible = false;
			newState.visible = true;
			curState = newState;
		}
	}
}
