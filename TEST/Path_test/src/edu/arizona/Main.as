package edu.arizona {
	import flash.display.Sprite;
	import flash.events.Event;

	[SWF(backgroundColor="0xFFFFFF" , width="880" , height="525", frameRate="30")]
	public class Main extends Sprite {
		
		private var menu:MainPage;
		private var game:GamePage;

		public function Main() {
			menu = new MainPage(stage.stageWidth, stage.stageHeight);
			menu.addEventListener("start", onStart, false, 0, true);
			menu.addEventListener("start-count", onStartCount, false, 0, true);
			addChild(menu);
			
			game = new GamePage(stage.stageWidth, stage.stageHeight);
			game.addEventListener("quit", onQuit, false, 0, true);
			game.visible = false;
			addChild(game);
		}
		
		private function onStart(evt:Event):void{
			menu.visible = false;
			game.visible = true;
			game.start();
		}
		
		private function onStartCount(evt:Event):void{
			menu.visible = false;
			game.startCount();
			game.visible = true;
		}
		
		private function onQuit(evt:Event):void{
			menu.visible = true;
			game.visible = false;
		}
	}
}
