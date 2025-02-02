function loadAboutUsPage(){
    const menuItems = document.querySelectorAll('.menu-list-item');
    menuItems.forEach(item => {
        item.classList.remove('active');
        const link = item.querySelector('a');
        if (link && link.textContent.trim() === 'Về chúng tôi') {
            item.classList.add('active');
        }
    });
}