package edu.arizona {
	import flash.display.Loader;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.net.URLRequest;
	import flash.system.Security;
	import flash.utils.Timer;

	public class YouTubePlayer {

		//rather than playing until the end of the video, play until this many turns
		private static const FPS:Number = 30; //29.970029;
		private static const FPSPS_1x:Number = 100; //25;
		private static const FPSPS_2x:Number = 200; //50;
		private static const FPSPS_4x:Number = 400; //100;
		private static var FPSPS:Number = FPSPS_1x;
		private static const STEP_SIZE:uint = FPS * 4; //100; //number of frames between clicks
		private static const NEW_MAX:uint = 10000000; //max number of frames of playtime
		private static var END:uint = NEW_MAX;
		private static const VID_ID1_1X:String = "lYG8s4Rl0cM"; //"wlY6aUIfe2w";
		private static const VID_ID1_2X:String = "1w66GztlIoY"; //"12xLthgyr-g";
		private static const VID_ID1_4X:String = "Ep1IFp1T2NI"; //"lYG8s4Rl0cM";
		public static const VID_ID1_WIDTH:uint = 960;
		public static const VID_ID1_HEIGHT:uint = 540;

		public static var width:uint;
		public static var height:uint;

		private var player:Object;
		private var loader:Loader = new Loader();

		private var targetTime:Number = 1;
		private var ready:Boolean;

		private var currentID:String;

		private var timer:Timer = new Timer(10);
		private var step:Number = STEP_SIZE;

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
			trace("WIDTH: " + w, "HEIGHT: " + h);
		}

		public function get video():Loader {
			return loader;
		}

		public function get videoid():uint {
			return 0;
		}

		private function onLoaderInit(event:Event):void {
			loader.content.addEventListener("onReady", onPlayerReady);
			loader.content.addEventListener("onError", onPlayerError);
			loader.content.addEventListener("onStateChange", onPlayerStateChange);
		}

		private function onPlayerReady(event:Event):void {
			trace("YOUTUBE PLAYER READY");
			player = loader.content;
			player.setSize(width, height);
			step = STEP_SIZE / 4;
			END = NEW_MAX / 4;
			currentID = VID_ID1_1X;
			FPSPS = FPSPS_1x;
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
//			trace("YOUTUBE STATE CHANGED: " + Object(event).data);
			if (Object(event).data == 0) {
				loader.dispatchEvent(new Event(Event.COMPLETE));
			}
		}

		public function onClick():Boolean {
			var currentTime:Number = Math.round(player.getCurrentTime() * YouTubePlayer.FPS) + 1;
			trace("CLICK: " + currentTime + " (" + player.getCurrentTime() + ")");
			if (currentTime < targetTime) {
				trace("not there yet");
				timer.start();
				player.playVideo();
				isReady = false;
				return false;
			}

			if (targetTime > END)
				return false;
			if (targetTime == END) {
				loader.dispatchEvent(new Event(Event.COMPLETE));
			}
			targetTime += step;
			timer.start();
			player.playVideo();
			isReady = false;
			return true;
		}

		public function getTime():Number {
			var currentTime:Number = Math.round(player.getCurrentTime() * FPSPS) + 1;
			trace("	" + currentTime + " frames = " + player.getCurrentTime() + " seconds X " + FPSPS + " fps");
//			if (currentID == VID_ID1_2X)
//				return currentTime * 2;
//			if (currentID == VID_ID1_4X)
//				return currentTime * 4;
			return currentTime;
		}

		private function onTick(evt:Event):void {
			var currentTime:Number = Math.round(player.getCurrentTime() * YouTubePlayer.FPS) + 1;
			if (currentTime >= targetTime) {
				player.pauseVideo();
				timer.stop();
				if (targetTime <= END)
					isReady = true;
				trace("TICK: " + currentTime + " >= " + targetTime);
			}
		}

		public function setSpeed1x():void {
			if (currentID != VID_ID1_1X) {
				if (currentID == VID_ID1_2X)
					targetTime *= 2;
				if (currentID == VID_ID1_4X)
					targetTime *= 4;
				FPSPS = FPSPS_1x;
				step = STEP_SIZE / 4; //1;
				END = NEW_MAX / 4; //1;
				currentID = VID_ID1_1X;
				player.loadVideoById(currentID, targetTime / FPS, "medium");
//				player.pauseVideo();

				targetTime -= step;
				onClick();
			}
		}

		public function setSpeed2x():void {
			if (currentID != VID_ID1_2X) {
				if (currentID == VID_ID1_1X)
					targetTime /= 2;
				if (currentID == VID_ID1_4X)
					targetTime *= 2;
				FPSPS = FPSPS_2x;
				step = STEP_SIZE / 8; //2;
				END = NEW_MAX / 8; //2;
				currentID = VID_ID1_2X;
				player.loadVideoById(currentID, targetTime / FPS, "medium");
//				player.pauseVideo();

				targetTime -= step;
				onClick();
			}
		}

		public function setSpeed4x():void {
			if (currentID != VID_ID1_4X) {
				if (currentID == VID_ID1_1X)
					targetTime /= 4;
				if (currentID == VID_ID1_2X)
					targetTime /= 2;
				FPSPS = FPSPS_4x;
				step = STEP_SIZE / 16; //4;
				END = NEW_MAX / 16; //4;
				currentID = VID_ID1_4X;
				player.loadVideoById(currentID, targetTime / FPS, "medium");
//				player.pauseVideo();

				targetTime -= step;
				onClick();
			}
		}

		public function seekBack():void {
			targetTime -= step;
			if (targetTime < step)
				targetTime = step;
			player.seekTo(((targetTime - step - 1) / YouTubePlayer.FPS), true);
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
			ready = b;
			if (b) {
				loader.dispatchEvent(new Event("isReady"));
			} else {
				loader.dispatchEvent(new Event("isNotReady"));
			}
		}

		public function get count():Number {
			if (currentID == VID_ID1_4X)
				return targetTime - STEP_SIZE / 4;
			else if (currentID == VID_ID1_2X)
				return targetTime - STEP_SIZE / 2;
			else
				return targetTime - STEP_SIZE;
//			return targetTime / step - 1;
		}

		public function get duration():Number {
			return player.getDuration() * FPS;
//			return NEW_MAX;
		}

		public function restart():void {
			player.seekTo(0);
			player.pauseVideo();
			targetTime = 0;
//			timer.start();
		}

		public function isFirstTime():Boolean {
			return targetTime <= 1;
		}
	}
}
