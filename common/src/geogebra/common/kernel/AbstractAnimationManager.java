package geogebra.common.kernel;

import java.util.ArrayList;
import java.util.TreeSet;

import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.geos.Animatable;
import geogebra.common.kernel.geos.GeoElement;

public abstract class AbstractAnimationManager {

	public final static int STANDARD_ANIMATION_TIME = 10; // secs
	public final static int MAX_ANIMATION_FRAME_RATE = 30; // frames per second
	public final static int MIN_ANIMATION_FRAME_RATE = 2; // frames per second
	protected Kernel kernel;
	protected ArrayList<GeoElement> animatedGeos;
	protected ArrayList<Animatable> changedGeos;
	protected double frameRate = MAX_ANIMATION_FRAME_RATE;
	private boolean needToShowAnimationButton;
	
	
	public AbstractAnimationManager(Kernel kernel2) {
		this.kernel = kernel2;
		animatedGeos = new ArrayList<GeoElement>();
		changedGeos = new ArrayList<Animatable>();
	}

	/**
	 * Returns whether the animation button needs to be drawn in the graphics
	 * view. This is only needed when there are animated geos with non-dynamic
	 * speed.
	 */
	final public boolean needToShowAnimationButton() {
		return needToShowAnimationButton;
	}

	/**
	 * Updates the needToShowAnimationButton value.
	 */
	public void updateNeedToShowAnimationButton() {
		int size = animatedGeos.size();
		if (size == 0) {
			needToShowAnimationButton = false;
			return;
		}

		// if one animated geo has a static speed, we need to get out of here
		for (int i = 0; i < size; i++) {
			GeoElement geo = animatedGeos.get(i);
			GeoElement animObj = geo.getAnimationSpeedObject();
			if (animObj == null || !animObj.isLabelSet()
					&& animObj.isIndependent()) {
				needToShowAnimationButton = true;
				return;
			}

		}

		// all animated geos have dynamic speed
		needToShowAnimationButton = false;
	}

	/**
	 * Adds geo to the list of animated GeoElements.
	 * 
	 * @param geo
	 *            the GeoElement to add
	 */
	final public synchronized void addAnimatedGeo(GeoElement geo) {
		if (geo.isAnimating() && !animatedGeos.contains(geo)) {
			animatedGeos.add((GeoElement) geo);
			// if (animatedGeos.size() == 1) removed, might have geos with
			// variable controlling speed
			updateNeedToShowAnimationButton();
		}
	}

	/**
	 * Removes geo from the list of animated GeoElements.
	 * 
	 * @param geo
	 *            the GeoElement to remove
	 */
	final public synchronized void removeAnimatedGeo(GeoElement geo) {
		if (animatedGeos.remove(geo) && animatedGeos.size() == 0) {
			stopAnimation();
		}
		updateNeedToShowAnimationButton(); // added, might have geos with
											// variable controlling speed
	}

	public synchronized void startAnimation() {
		if (!isRunning() && animatedGeos.size() > 0) {
			updateNeedToShowAnimationButton();
			startTimer();
		}
	}

	public synchronized void stopAnimation() {
		if (isRunning()) {
			stopTimer();
			updateNeedToShowAnimationButton();
		}
	}
	
	/**
	 * Returns whether the animation is currently paused, i.e. the animation is
	 * not running but there are elements with "Animation on" set.
	 */
	public boolean isPaused() {
		return !isRunning() && animatedGeos.size() > 0;
	}
	
	public void clearAnimatedGeos() {
		for (int i = 0; i < animatedGeos.size(); i++) {
			GeoElement geo = animatedGeos.get(i);
			geo.setAnimating(false);
		}

		animatedGeos.clear();
		updateNeedToShowAnimationButton();
	}
	
	/**
	 * Adapts the frame rate depending on how long it took to compute the last
	 * frame.
	 * 
	 * @param frameTime
	 */
	private void adaptFrameRate(long compTime) {
		// only allow to use 80% of CPU time for animation (800 millis out of 1
		// sec)
		double framesPossible = 800.0 / compTime;

		// the frameRate is too high: decrease it
		if (framesPossible < frameRate) {
			frameRate = Math.max(framesPossible, MIN_ANIMATION_FRAME_RATE);
			setTimerDelay((int) Math.round(1000.0 / frameRate));

			// System.out.println("DECREASED frame rate: " + frameRate +
			// ", framesPossible: " + framesPossible);
		}

		// the frameRate is too low: try to increase it
		else if (frameRate < MAX_ANIMATION_FRAME_RATE) {
			frameRate = Math.min(framesPossible, MAX_ANIMATION_FRAME_RATE);
			setTimerDelay((int) Math.round(1000.0 / frameRate));

			// System.out.println("INCREASED frame rate: " + frameRate +
			// ", framesPossible: " + framesPossible);
		}

	}
	
	private TreeSet<AlgoElementInterface> tempSet;

	private TreeSet<AlgoElementInterface> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElementInterface>();
		}
		return tempSet;
	}
	
	protected void sliderStep(){
		// skip animation frames while kernel is saving XML
				if (kernel.isSaving())
					return;

				long startTime = System.currentTimeMillis();

				// clear list of geos that need to be updated
				changedGeos.clear();

				// perform animation step for all animatedGeos
				int size = animatedGeos.size();
				for (int i = 0; i < size; i++) {
					Animatable anim = (Animatable) animatedGeos.get(i);
					boolean changed = anim.doAnimationStep(frameRate);
					if (changed)
						changedGeos.add(anim);
				}

				// do we need to update anything?
				if (changedGeos.size() > 0) {
					// efficiently update all changed GeoElements
					GeoElement.updateCascade(changedGeos, getTempSet(), false);

					// repaint views
					kernel.notifyRepaint();

					// check frame rate
					long compTime = System.currentTimeMillis() - startTime;
					adaptFrameRate(compTime);

					// System.out.println("UPDATE compTime: " + compTime +
					// ", frameRate: " + frameRate);
				}

	
	}
	
	/**
	 * Returns whether the animation is currently running.
	 */
	public abstract boolean isRunning();
	
	protected abstract void setTimerDelay(int i);
	protected abstract void stopTimer();
	protected abstract void startTimer();

}
