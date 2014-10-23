package edu.arizona {
	import flash.display.Loader;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.net.URLRequest;
	import flash.system.Security;
	import flash.utils.Timer;

	public class YouTubePlayer {

		//rather than playing until the end of the video, play until this many turns
		private static const NEW_MAX:uint = 20;
		private static var END:uint = NEW_MAX;
		private static const VID_ID_1X:String = "YQa9xPWVgUo";
		private static const VID_ID_2X:String = "YoAla7m81w8";
		private static const VID_ID_4X:String = "hh7Q3SIrPcA";

		private var player:Object;
		private var loader:Loader = new Loader();
		private var width:uint;
		private var height:uint;
		private var targetTime:Number = 0;
		private var ready:Boolean;
		
		private var currentID:String;

		private var timer:Timer = new Timer(10);
		private var step:Number = 1;

		public function YouTubePlayer(w:uint, h:uint) {
			try {
				Security.allowDomain("*");
			} catch (e:Error) {

			}

			loader.contentLoaderInfo.addEventListener(Event.INIT, onLoaderInit);
			loader.load(new URLRequest("http://www.youtube.com/apiplayer?version=3"));
			loader.mouseEnabled = false;
			loader.mouseChildren = false;

			width = w;
			height = h;
		}

		public function get video():Loader {
			return loader;
		}

		private function onLoaderInit(event:Event):void {
			loader.content.addEventListener("onReady", onPlayerReady);
			loader.content.addEventListener("onError", onPlayerError);
			loader.content.addEventListener("onStateChange", onPlayerStateChange);
		}

		private function onPlayerReady(event:Event):void {
			player = loader.content;
			player.setSize(width, height);
			currentID = VID_ID_1X;
			player.loadVideoById(currentID, 0, "medium");
			player.pauseVideo();
			timer.addEventListener(TimerEvent.TIMER, onTick, false, 0, true);
			timer.start();
			isReady = true;
		}

		private function onPlayerError(event:Event):void {
			trace("player error:", Object(event).data);
		}

		private function onPlayerStateChange(event:Event):void {
			if (Object(event).data == 0) {
				loader.dispatchEvent(new Event(Event.COMPLETE));
			}
		}

		public function onClick():void {
			trace("CLICK: " + player.getCurrentTime());
			if(targetTime == step){
				
			}
			if (targetTime > END)
				return;
			if (targetTime == END) {
				loader.dispatchEvent(new Event(Event.COMPLETE));
			}
			targetTime += step;
			timer.start();
			player.playVideo();
			isReady = false;
		}

		private function onTick(evt:Event):void {
			if (player.getCurrentTime() >= targetTime) {
				player.pauseVideo();
				timer.stop();
				if (targetTime <= END)
					isReady = true;
				trace("TICK: " + player.getCurrentTime() + " >= " + targetTime);
			}
		}
		
		public function setSpeed1x():void{
			if(currentID != VID_ID_1X){
				if(currentID == VID_ID_2X)
					targetTime *= 2;
				if(currentID == VID_ID_4X)
					targetTime *= 4;
				step = 1;
				END = NEW_MAX;
				currentID = VID_ID_1X;
				player.loadVideoById(currentID, targetTime, "medium");
				player.pauseVideo();
			}
		}
		
		public function setSpeed2x():void{
			if(currentID != VID_ID_2X){
				if(currentID == VID_ID_1X)
					targetTime /= 2;
				if(currentID == VID_ID_4X)
					targetTime *= 2;
				step = 1/2;
				END = NEW_MAX / 2;
				currentID = VID_ID_2X;
				player.loadVideoById(currentID, targetTime, "medium");
				player.pauseVideo();
			}
		}
		
		public function setSpeed4x():void{
			if(currentID != VID_ID_4X){
				if(currentID == VID_ID_1X)
					targetTime /= 4;
				if(currentID == VID_ID_2X)
					targetTime /= 2;
				step = 1/4;
				END = NEW_MAX / 4;
				currentID = VID_ID_4X;
				player.loadVideoById(currentID, targetTime, "medium");
				player.pauseVideo();
			}
		}

		public function seekBack():void {
			targetTime -= step;
			if (targetTime < step)
				targetTime = step;
			player.seekTo(targetTime - step, true);
//			trace("UNDO START: " + player.getCurrentTime());
			timer.start();
			player.playVideo();
			isReady = false;
		}

		public function seekForward():void {
			targetTime += step;
//			trace("REDO START: " + player.getCurrentTime());
			timer.start();
			player.playVideo();
			isReady = false;
		}

		public function get isReady():Boolean {
			return ready;
		}

		public function set isReady(b:Boolean):void {
			if (b) {
				loader.dispatchEvent(new Event("isReady"));
			} else {
				loader.dispatchEvent(new Event("isNotReady"));
			}
			ready = b;
		}

		public function get count():Number {
			return targetTime / step - 1;
		}

		public function get duration():Number {
//			return player.getDuration();
			return NEW_MAX;
		}

		public function restart():void {
			player.seekTo(0);
			player.pauseVideo();
			targetTime = 0;
//			timer.start();
		}
		
		public function isFirstTime():Boolean {
			return targetTime==0;
		}
	}
}
