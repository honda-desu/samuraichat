function openProfileModal(el) {
    const name = el.getAttribute("data-name");
    const text = el.getAttribute("data-text");
    const image = el.getAttribute("data-image");
    const userId = el.getAttribute("data-user-id"); // ★ 追加


    document.getElementById("modalUserName").textContent = name;
    document.getElementById("modalUserText").textContent = text;
    document.getElementById("modalProfileImage").src = image;
    
    // ★ DMボタンのリンクを動的に設定
    const dmButton = document.getElementById("modalDmButton");
    dmButton.href = "/dm/room/" + userId;

    document.getElementById("profileModal").style.display = "flex";
}

function closeProfileModal() {
    document.getElementById("profileModal").style.display = "none";
}