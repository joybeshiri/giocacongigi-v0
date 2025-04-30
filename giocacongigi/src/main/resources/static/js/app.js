const API_AUTH   = "http://localhost:8080/giocacongigi/api/auth";
const API_USER   = "http://localhost:8080/giocacongigi/api/user";
const API_EVENTS = "http://localhost:8080/giocacongigi/api/events";

let currentUser = null;

function showPage(page) {
  $('.page').hide();
  $('#btn-logout').hide();
  $('form').each(function() {
    this.reset();
  });

  switch (page) {
    case "home":
      break;
    case "login":
      break;
    case "register":
      break;
    case "user":
      visualizzaEventi();
      $('#btn-logout').show();
      break;
    case "admin":
      $('#btn-logout').show();
      break;
    case "create-event":
      $('#btn-logout').show();
      break;
    case "view-event":
      visualizzaEventi("view");
      $('#btn-logout').show();
  }

  $("#page-" + page).show();
}

function visualizzaEventi(view = "") {
  const token = localStorage.getItem("token");
  if (!token || !currentUser) return showPage("login");

  $("#loadingSpinner").show();

  $.ajax({
    url: API_EVENTS + "/joinable/" + currentUser.id,
    method: "GET",
    headers: { Authorization: "Bearer " + token },
    success: function (tabellaEventi) {
      let counter = 0;
      let tableId = (view === "view") ? "#tabellaEventiView" : "#tabellaEventi";
      $(tableId).empty();
      tabellaEventi.forEach(function (event) {
        counter++;
        let action = event.joinable ? "subscribe" : "unsubscribe";
        let btn_text = event.joinable ? "iscriviti" : "annulla iscrizione";
        $(tableId).append(`
          <tr>
            <td>${counter}</td>
            <td>${event.playDate}</td>
            <td>${event.playTime}</td>
            <td>${event.description}</td>
            <td>${event.playingField.name}</td>
            <td>${event.users.length}</td>
            <td>
              <a href='#' onclick='subscribeOrUnsubscribe(${event.id}, ${currentUser.id}, "${action}")' class='btn btn-primary btn-sm'>${btn_text}</a>
            </td>
          </tr>
        `);
      });
    },
    error: function (jqXHR, textStatus, errorThrown) {
      showHttpError("Errore durante il caricamento degli eventi", jqXHR, textStatus, errorThrown);
    },
    complete: function () {
      $("#loadingSpinner").hide();
    },
  });
}

function subscribeOrUnsubscribe(eventId, userId, action) {
  const token = localStorage.getItem("token");
  if (!token || !currentUser) return showPage("login");

  let method = '';
  let message = '';

  switch (action) {
    case 'subscribe':
      method = 'POST';
      message = 'Iscrizione effettuata con successo';
      break;
    case 'unsubscribe':
      method = 'DELETE';
      message = 'Disiscrizione effettuata con successo';
      break;
    default:
      return;
  }

  $.ajax({
    url: API_EVENTS + "/" + action,
    method: method,
    headers: { Authorization: "Bearer " + token },
    contentType: "application/json",
    data: JSON.stringify({ eventId, userId }),
    success: function () {
      alert(message);
      visualizzaEventi();
    },
    error: function (jqXHR, textStatus, errorThrown) {
      showHttpError("Errore durante Iscrizione/Disiscrizione", jqXHR, textStatus, errorThrown);
    }
  });
}

function doClick(event, btnId) {
  event.preventDefault();

  let page = "";
  switch (btnId) {
    case 'btn-logout':
      localStorage.clear();
      currentUser = null;
      page = "home";
      break;
    default:
      page = btnId.replace("btn-", "");
      break;
  }
  showPage(page)
}

function showHttpError(message, jqXHR, textStatus, errorThrown) {
  $('#emmMessage').text(message);
  $('#emmStatus').text(jqXHR.status);
  $('#emmStatusText').text(jqXHR.statusText);
  $('#emmResponseText').text(jqXHR.responseText);

  var errorModal = new bootstrap.Modal(document.getElementById('errorMessageModal'), {
    backdrop: 'static',
    keyboard: false
  });

  errorModal.show();
}

$(document).ready(function () {
  showPage("home");

  $('#btn-home').click(function (event) { doClick(event, this.id); });
  $('#btn-login').click(function (event) { doClick(event, this.id); });
  $('#btn-register').click(function (event) { doClick(event, this.id); });
  $('#btn-logout').click(function (event) { doClick(event, this.id); });
  $('#btn-create-event').click(function (event) { doClick(event, this.id); });
  $('#btn-view-event').click(function (event) { doClick(event, this.id); });

  $('#login-form').submit(function (e) {
    e.preventDefault();

    const email = $('#login-email').val();
    const password = $('#login-password').val();

    $.ajax({
      url: API_AUTH + "/login",
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify({ email, password }),
      success: function (response) {
        localStorage.setItem("token", response.token);
        getProfile();
      },
      error: function (jqXHR, textStatus, errorThrown) {
        showHttpError("Errore durante il login", jqXHR, textStatus, errorThrown);
      }
    });
  });

  $('#registerForm').submit(function (e) {
    e.preventDefault();

    const name = $('#registration-name').val();
    const email = $('#registration-email').val();
    const password = $('#registration-password').val();
    const role = "user";

    $.ajax({
      url: API_AUTH + "/register",
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify({ name, email, password, role }),
      success: function () {
        alert("Registrazione completata");
        showPage("login");
      },
      error: function (jqXHR, textStatus, errorThrown) {
        showHttpError("Errore durante la registrazione", jqXHR, textStatus, errorThrown);
      }
    });
  });

  function getProfile() {
    const token = localStorage.getItem("token");
    if (!token) return showPage("login");

    $.ajax({
      url: API_USER + "/me",
      method: "GET",
      headers: { Authorization: "Bearer " + token },
      success: function (user) {
        currentUser = user;
        showPage(currentUser.role);
      },
      error: function (jqXHR, textStatus, errorThrown) {
        showHttpError("Errore nel recupero dei dati dell'utente", jqXHR, textStatus, errorThrown);
        logout();
      }
    });
  }
});

function createEvent() {
  const token = localStorage.getItem("token");
  if (!token || !currentUser) return showPage("login");

  const name = $('#event-name').val();
  const date = $('#event-date').val();
  const time = $('#event-time').val();
  const description = $('#event-description').val();
  const location = $('#event-location').val();

  $.ajax({
    url: API_EVENTS,
    method: "POST",
    headers: { Authorization: "Bearer " + token },
    contentType: "application/json",
    data: JSON.stringify({ name, date, time, description, location }),
    success: function () {
      alert("Evento creato con successo");
      showPage("admin");
    },
    error: function (jqXHR, textStatus, errorThrown) {
      showHttpError("Errore durante la creazione dell'evento", jqXHR, textStatus, errorThrown);
    }
  });
}
