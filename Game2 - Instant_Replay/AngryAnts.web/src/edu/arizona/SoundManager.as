package edu.arizona 
{
	/**
	 * ...
	 * @author Zuoming
	 */
	import flash.media.Sound;
	import flash.media.SoundChannel;  

	public class SoundManager 
	{
		public var sndChannel:SoundChannel
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/sounds/write.mp3")]
		private var sndCls:Class;
		private var write:Sound = new sndCls() as Sound; 
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/sounds/click.mp3")]
		private var sndCls2:Class;
		private var click:Sound = new sndCls2() as Sound; 
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/sounds/hover.mp3")]
		private var sndCls3:Class;
		private var hover:Sound = new sndCls3() as Sound; 
		[Embed(source = "C:/Dropbox/AngryAnts/Game2/AngryAnts.web/sounds/button_click.mp3")]
		private var sndCls4:Class;
		private var button_click:Sound = new sndCls4() as Sound; 
		
		public function SoundManager() 
		{}
		
		public function write_play(){
			sndChannel = write.play();
		}
		
		public function click_play(){
			sndChannel = click.play();
		}
		
		public function hover_play(){
			sndChannel = hover.play();
		}
		
		public function button_click_play(){
			sndChannel = button_click.play();
		}
	}

}