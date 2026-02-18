function openProfileModal(el) {
    const name = el.getAttribute("data-name");
    const text = el.getAttribute("data-text");
    const image = el.getAttribute("data-image");

    document.getElementById("modalUserName").textContent = name;
    document.getElementById("modalUserText").textContent = text;
    document.getElementById("modalProfileImage").src = image;

    document.getElementById("profileModal").style.display = "flex";
}

function closeProfileModal() {
    document.getElementById("profileModal").style.display = "none";
}