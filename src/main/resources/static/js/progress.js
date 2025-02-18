const form = document.querySelector("#importform")
form.addEventListener('submit', openProgressBar)

function openProgressBar(event) {
  const progressbar = document.querySelector("#progressbar")
  progressbar.setAttribute("style", "visibility: visible")
}
