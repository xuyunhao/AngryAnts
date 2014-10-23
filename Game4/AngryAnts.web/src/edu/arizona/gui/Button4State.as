package edu.arizona.gui {
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	public class Button4State extends Button3State {

		private var state4:DisplayObject;
		private var disabled:Boolean = false;
		private var isSwitch:Boolean;

		public function Button4State(stateUp:DisplayObject, stateOver:DisplayObject, stateDown:DisplayObject, stateDisabled:DisplayObject, switchButton:Boolean = false) {
			super(stateUp, stateOver, stateDown);
			isSwitch = switchButton;

			state4 = stateDisabled;
			state4.visible = false;
			addChild(stateDisabled);
		}

		public function set isDisabled(b:Boolean):void {
			disabled = b;
			if (!isSwitch) {
				if (disabled) {
					changeState(state4);
					this.removeEventListener(MouseEvent.MOUSE_DOWN, onDown);
					this.removeEventListener(MouseEvent.MOUSE_UP, onUp);
					this.removeEventListener(MouseEvent.MOUSE_OVER, onOver);
					this.removeEventListener(MouseEvent.MOUSE_OUT, onOut);
					this.mouseEnabled = false;
				} else {
					changeState(state1);
					this.addEventListener(MouseEvent.MOUSE_DOWN, onDown, false, 0, true);
					this.addEventListener(MouseEvent.MOUSE_UP, onUp, false, 0, true);
					this.addEventListener(MouseEvent.MOUSE_OVER, onOver, false, 0, true);
					this.addEventListener(MouseEvent.MOUSE_OUT, onOut, false, 0, true);
					this.mouseEnabled = true;
				}
			} else {
				if (disabled) {
					changeState(state4);
				} else {
					changeState(state1);
				}
			}
		}

		protected override function onOut(evt:MouseEvent):void {
			if (disabled) {
				changeState(state4);
			} else {
				changeState(state1);
			}
		}

		public function get isDisabled():Boolean {
			return disabled;
		}
	}
}
