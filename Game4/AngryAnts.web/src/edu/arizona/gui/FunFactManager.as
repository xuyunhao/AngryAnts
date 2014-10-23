package edu.arizona.gui {
	import edu.arizona.Main;
	
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.MouseEvent;
	import flash.net.URLLoader;
	import flash.net.URLLoaderDataFormat;
	import flash.net.URLRequest;
	import flash.text.TextField;
	import flash.text.TextFieldAutoSize;
	import flash.text.TextFormat;

	public class FunFactManager extends Sprite {

		private var factList:Array;
		private var i:uint;
		private var counter:int;

		private var factBG:Sprite;
		private var fact:TextField;

		public function FunFactManager() {
			initFactData();
		}

		private function initFactData():void {
			var loader:URLLoader = new URLLoader();
			loader.addEventListener(Event.COMPLETE, loaded);
			loader.addEventListener(IOErrorEvent.IO_ERROR, onIOError);
			var request:URLRequest = new URLRequest("http://angryants.cs.arizona.edu/data/funfacts.txt");
			loader.load(request);
			function onIOError(event:IOErrorEvent):void {
				trace("Error: receiveStart()." + event);
			}
			function loaded(evt:Event):void {
				factList = loader.data.split("\r\n\r\n");
				initFactDisplay();
			}
		}

		private function initFactDisplay():void {
			factBG = new Sprite();
			addChild(factBG);

			fact = new TextField();
			fact.defaultTextFormat = new TextFormat("Tahoma", 16, 0xFFFFFF);
			fact.autoSize = TextFieldAutoSize.LEFT;
			fact.multiline = true;
			fact.wordWrap = true;
			fact.selectable = false;
			fact.width = 200;
			fact.x = fact.y = 5;
			addChild(fact);

			counter = 2;

			this.addEventListener(MouseEvent.MOUSE_OVER, onMouseOver);
			this.addEventListener(MouseEvent.MOUSE_OUT, onMouseOut);
			this.mouseEnabled = true;
		}

		private function onMouseOver(evt:MouseEvent):void {
			alpha = .1;
		}

		private function onMouseOut(evt:MouseEvent):void {
			alpha = 1;
		}

		private function nextFact():void {
			fact.text = factList[i];

			factBG.graphics.clear();
			factBG.graphics.lineStyle(1, 0xFFFFFF, 1, true);
			factBG.graphics.beginFill(0x006600);
			factBG.graphics.drawRoundRect(0, 0, fact.width + 10, fact.height + 10, 20, 20);
			factBG.graphics.endFill();

			i = (i + 1) % factList.length;
		}

		private var h:Boolean = false;
		
		public function get hide():Boolean{
			return h;
		}
		
		public function set hide(b:Boolean):void{
			h = b;
			if(!b){
				if(counter > 0)
					visible = false;
				else
					visible = true;
			}else{
				visible = false;
			}
		}

		public function onClick(cx:Number, cy:Number):void {
			if (hide)
				return;
			if (counter == 0) {
				nextFact();
				
				y = cy - height / 2;
				if (y < 10)
					y = 10;
				var bot:Number = Main.STAGE_HEIGHT - 20 - HudBar.HUD_HEIGHT - 10;
				if (y + height > bot)
					y = bot - height;
				
//				x = Main.STAGE_WIDTH - width - 20;
//				y = Main.STAGE_HEIGHT - height - 50 - 20;
				visible = true;
			} else if (counter == -3) {
				visible = false;
				counter = 2;
			}
			counter--;
			
			if (cx < Main.STAGE_WIDTH / 2) {
				x = Main.STAGE_WIDTH/2 + 200;
			} else {
				x = Main.STAGE_WIDTH/2 - 400;// - width;
			}
			
		}

		public function prepare():void {
			visible = false;
			counter = 2;
		}
	}
}
