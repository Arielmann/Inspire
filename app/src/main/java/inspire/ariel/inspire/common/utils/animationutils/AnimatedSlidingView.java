package inspire.ariel.inspire.common.utils.animationutils;

import android.animation.Animator;
import android.view.View;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data @Builder
public class AnimatedSlidingView {

    @NonNull private View view;
    @NonNull private float initialYPos;
    @NonNull private float endAnimatedYPos;

    private void slideIn() {
        view.animate().y(initialYPos);
    }

    private void slideOut(){
        view.animate().y(endAnimatedYPos);
    }

    private final Animator.AnimatorListener slideInWhenOtherAnimationEnded = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            slideIn();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private final Animator.AnimatorListener slideOutWhenOtherAnimationEnded = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            slideOut();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };
}
