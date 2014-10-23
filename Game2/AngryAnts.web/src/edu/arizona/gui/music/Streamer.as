package  edu.arizona.gui.music
{
	import edu.arizona.gui.HudBar;
	import flash.media.Sound;
	import flash.media.SoundLoaderContext;
	import flash.net.URLRequest;
	import flash.media.SoundChannel;
	import flash.media.SoundTransform;
	import flash.events.Event;
	/**
	 * ...
	 * @author Zuoming
	 */
	public class Streamer 
	{/*
		public var soundUrl:Array = new Array("http://angryants.cs.arizona.edu/music/song1.mp3",
											"http://angryants.cs.arizona.edu/music/song2.mp3",
											"http://angryants.cs.arizona.edu/music/song3.mp3");*/
		//stream.nextSong();
		public var soundUrl:Array = new Array("http://angryants.cs.arizona.edu/music/Kalalau.mp3",
											"http://angryants.cs.arizona.edu/music/Estrellas.mp3",
											"http://angryants.cs.arizona.edu/music/Elevator.mp3",
											"http://angryants.cs.arizona.edu/music/Aces_High.mp3");
		public static const NUM_SONGS:int = 4;
		public var sndChannel:SoundChannel;
		public var context:SoundLoaderContext = new SoundLoaderContext(8000, true);
		public var currentMusic:int = NUM_SONGS;
		private var hb:HudBar;
		
		public function Streamer(hudbar:HudBar)
		{
			hb = hudbar;
			sndChannel = new SoundChannel();
			//nextSong();
		}
		
		public function nextSong():void {
			sndChannel.stop();
			var index:int = Math.floor(Math.random() * NUM_SONGS);
			while (index == currentMusic) {
				index = Math.floor(Math.random() * NUM_SONGS);
			}
			var req:URLRequest = new URLRequest(soundUrl[index]);
			var s:Sound = new Sound();
			s.load(req, context);
			sndChannel = s.play();
			currentMusic = index;
			sndChannel.addEventListener(Event.SOUND_COMPLETE, soundCompleteHandler);
			if (!hb.haveSound) {	
				var mute:SoundTransform = new SoundTransform(0, 0);
				sndChannel.soundTransform = mute;
			}
		}
		
		public function checkMusic():void {
			if (sndChannel.position == 0){
				sndChannel.removeEventListener(Event.SOUND_COMPLETE, soundCompleteHandler);
				nextSong();
				sndChannel.addEventListener(Event.SOUND_COMPLETE, soundCompleteHandler);
			}
		}
		
		private function soundCompleteHandler(event:Event):void {
            trace("soundCompleteHandler: " + event);
			sndChannel.removeEventListener(Event.SOUND_COMPLETE, soundCompleteHandler);
			nextSong();
			sndChannel.addEventListener(Event.SOUND_COMPLETE, soundCompleteHandler);
		}
	}

}