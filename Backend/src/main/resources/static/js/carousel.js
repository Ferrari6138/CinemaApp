document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.film-carousel').forEach(setupCarousel);
});

function setupCarousel(carousel) {
    const track = carousel.querySelector('.film-carousel-track');
    const leftArrow = carousel.querySelector('.carousel-arrow-left');
    const rightArrow = carousel.querySelector('.carousel-arrow-right');
    if (!track || !leftArrow || !rightArrow) return;

    const EDGE_ZONE = 0.15; // mostra a seta quando o ponteiro entra nos 15% da borda

    function canScrollLeft() { return track.scrollLeft > 4; }
    function canScrollRight() {
        return track.scrollLeft < track.scrollWidth - track.clientWidth - 4;
    }

    function updateArrowsFromPointer(clientX) {
        const rect = carousel.getBoundingClientRect();
        const ratio = (clientX - rect.left) / rect.width;

        leftArrow.classList.toggle('visible', ratio <= EDGE_ZONE && canScrollLeft());
        rightArrow.classList.toggle('visible', ratio >= (1 - EDGE_ZONE) && canScrollRight());
    }

    carousel.addEventListener('mousemove', (e) => updateArrowsFromPointer(e.clientX));
    carousel.addEventListener('mouseleave', () => {
        leftArrow.classList.remove('visible');
        rightArrow.classList.remove('visible');
    });

    track.addEventListener('scroll', () => {
        if (!canScrollLeft()) leftArrow.classList.remove('visible');
        if (!canScrollRight()) rightArrow.classList.remove('visible');
    });

    leftArrow.addEventListener('click', () => {
        track.scrollBy({ left: -track.clientWidth * 0.8, behavior: 'smooth' });
    });
    rightArrow.addEventListener('click', () => {
        track.scrollBy({ left: track.clientWidth * 0.8, behavior: 'smooth' });
    });
}
