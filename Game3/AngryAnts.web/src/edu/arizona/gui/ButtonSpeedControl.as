package edu.arizona.gui {
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;

	public class ButtonSpeedControl extends Button3State {

		private static var selected:ButtonSpeedControl;

		public static function makeSelected(b:ButtonSpeedControl):void {
			if(selected)
				selected.changeState(selected.state1);
			selected = b;
			selected.changeState(selected.state3);
		}

		public function ButtonSpeedControl(stateUp:DisplayObject, stateOver:DisplayObject, stateDown:DisplayObject) {
			super(stateUp, stateOver, stateDown);
		}

		protected override function onOut(evt:MouseEvent):void {
			if (selected == this)
				changeState(state3);
			else
				changeState(state1);
		}
	}
}
