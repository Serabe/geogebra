package geogebra.web.kernel;

import geogebra.common.kernel.AbstractAnimationManager;

import geogebra.common.kernel.Kernel;
import geogebra.web.kernel.gawt.Timer;

public class AnimationManager extends AbstractAnimationManager implements HasTimerAction{
	private Timer timer;
	public AnimationManager(Kernel kernel2) {
	    super(kernel2);
	    timer = new Timer(1000 / MAX_ANIMATION_FRAME_RATE,this);
    }

	@Override
    public boolean isRunning() {
	    return timer.isRunning();
    }

	@Override
    protected void setTimerDelay(int i) {
	    timer.setDelay(i);
    }

	@Override
    protected void stopTimer() {
		timer.stop();
    }

	@Override
    protected void startTimer() {
		timer.start();
	    
    }

	public void actionPerformed() {
	    sliderStep();
    }

	

}
