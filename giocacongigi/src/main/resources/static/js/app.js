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
      caricaCampiDaGioco();
      break;
      case "delete-event":
        $('#btn-logout').show();
        visualizzaEventiPerEliminazione();
        break;
    case "view-event":
      visualizzaEventi("view");
      $('#btn-logout').show();
        break;
    case "change-password":
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
        let btn="";
        if (currentUser.role == "admin") {
         btn = "<a href='#' onclick='editEvent(" + JSON.stringify(event).replace(/'/g, "\\'") + ")' class='btn btn-warning btn-sm'>Modifica</a>";
        } else {
          let action = event.joinable ? "subscribe" : "unsubscribe";
          let btn_text = event.joinable ? "iscriviti" : "annulla iscrizione";
          btn = "<a href='#' onclick='subscribeOrUnsubscribe(" + event.id + ", " + currentUser.id + ", \"" + action + "\")' class='btn btn-primary btn-sm'>" + btn_text + "</a>";
        }
        $(tableId).append(`
          <tr>
            <td>${counter}</td>
            <td>${event.playDate}</td>
            <td>${event.playTime}</td>
            <td>${event.description}</td>
            <td>${event.playingField.name}</td>
            <td>${event.users.length}</td>
            <td>${btn}</td>
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

  $('#btn-create-event').click(function () {
    showPage("create-event");
  });

  $('#btn-delete-event').click(function (event) {
    event.preventDefault();
    showPage("delete-event");
  });
  $('#btn-back-to-admin').click(function (event) {
    event.preventDefault();
    showPage("admin");
  });
//btn-back-to-admin serve a tornare alla console è ripetitivo ma almeno ha un suo percorso invece di condividerlo con visualizza eventi

  $('#create-event-form').submit(function (e) {
    e.preventDefault();
    createEvent();
  });

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

$(document).ready(function () {
  // Gestione del click per il pulsante "Torna alla console di amministrazione" nella pagina crea evento
  $('#btn-back-to-events-create').click(function (event) {
    event.preventDefault();
    showPage("admin");  // Torna alla pagina admin (console di amministrazione)
  });

  // Gestione del click per il pulsante "Torna alla console di amministrazione" nella pagina visualizza evento
  $('#btn-back-to-events-view').click(function (event) {
    event.preventDefault();
    showPage("admin");  // Torna alla pagina admin (console di amministrazione)
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

function caricaCampiDaGioco(callback) {
    const token = localStorage.getItem("token");

    if (!token) {
        console.error("Token mancante!");
        alert("Token mancante! Assicurati di essere autenticato.");
        return;
    }

    $.ajax({
        url: "http://localhost:8080/giocacongigi/api/fields",
        method: "GET",
        headers: { Authorization: "Bearer " + token },
        success: function (data) {
            console.log("Dati dei campi ricevuti:", data);

            const selectCreate = $('#event-location');
            const selectEdit = $('#event-edit-location');

            selectCreate.empty().append('<option value="">-- Seleziona un campo --</option>');
            selectEdit.empty().append('<option value="">-- Seleziona un campo --</option>');

            data.forEach(function (campo) {
                selectCreate.append(`<option value="${campo.id}" title="${campo.description}">${campo.name}</option>`);
                selectEdit.append(`<option value="${campo.id}" title="${campo.description}">${campo.name}</option>`);
            });

            if (typeof callback === "function") {
                callback(); // Esegui la callback dopo aver caricato i dati
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Errore durante il caricamento dei campi da gioco:", errorThrown);
            alert("Errore nel caricamento dei campi da gioco. Per favore riprova.");
        }
    });
}



function createEvent() {
  const token = localStorage.getItem("token");

  const playDate = $('#event-date').val();
  const playTime = $('#event-time').val();
  const description = $('#event-description').val();
  const playingFieldId = $('#event-location').val();

  $.ajax({
    url: API_EVENTS,
    method: "POST",
    headers: { Authorization: "Bearer " + token },
    contentType: "application/json",
    data: JSON.stringify({ playDate, playTime, description, playingFieldId }),
    success: function () {
      alert("Evento creato con successo");
      showPage("admin");
    },
    error: function (jqXHR, textStatus, errorThrown) {
      showHttpError("Errore durante la creazione dell'evento", jqXHR, textStatus, errorThrown);
    }
  });
}

function deleteEvent(eventId) {
    const token = localStorage.getItem("token");
    if (!token || !currentUser || currentUser.role !== "admin") {
        alert("Accesso non autorizzato.");
        return showPage("login"); // Se l'utente non è autenticato o non è admin, lo rimandiamo al login
    }

    if (confirm("Sei sicuro di voler eliminare questo evento?")) {
        // Effettuiamo la richiesta DELETE
        $.ajax({
            url: API_EVENTS + "/" + eventId,
            method: "DELETE",
            headers: { Authorization: "Bearer " + token },
            success: function () {
                alert("Evento eliminato con successo!");
                visualizzaEventiPerEliminazione(); // Ricarica la lista degli eventi da eliminare
            },
            error: function (jqXHR, textStatus, errorThrown) {
                showHttpError("Errore durante l'eliminazione dell'evento", jqXHR, textStatus, errorThrown);
            }
        });
    }
}



function visualizzaEventiAdmin(view = "") {
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

// MOSDIFICA EVENTI NELLA VISUALIZZA
function editEvent(event) {
  $("#edit-event-id").val(event.id);
  $("#edit-event-date").val(event.playDate);
  $("#edit-event-time").val(event.playTime);
  $("#edit-event-description").val(event.description);

  // Carica i campi da gioco e solo dopo imposta il valore selezionato
  caricaCampiDaGioco(function () {
  $("#edit-event-id").val(event.id);
  $("#edit-event-date").val(event.playDate);
  $("#edit-event-time").val(event.playTime);
  $("#edit-event-description").val(event.description);
  $("#event-edit-location").val(event.playingField.id);
  });

  showPage("edit-event");
}


$("#form-edit-event").submit(function (e) {
    e.preventDefault();
    const token = localStorage.getItem("token");

    const updatedEvent = {
        id: $("#edit-event-id").val(),
        playDate: $("#edit-event-date").val(),
        playTime: $("#edit-event-time").val(),
        description: $("#edit-event-description").val(),
        playingFieldId: $("#event-edit-location").val(), // ID campo selezionato
    };

    if (!updatedEvent.playDate || !updatedEvent.playTime || !updatedEvent.playingFieldId) {
        alert("Per favore, completa tutti i campi obbligatori.");
        return;
    }

    $.ajax({
        url: API_EVENTS + "/" + updatedEvent.id, // URL per aggiornare l'evento
        method: "PUT",
        contentType: "application/json",
        data: JSON.stringify(updatedEvent),
        headers: { Authorization: "Bearer " + token },
        success: function () {
            alert("Evento aggiornato con successo!");
            visualizzaEventi("view");
            showPage("view-event");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Errore durante l'aggiornamento dell'evento", errorThrown);
            alert("Errore durante l'aggiornamento dell'evento.");
        }
    });
});

function visualizzaEventiPerEliminazione() {
    const token = localStorage.getItem("token");
    if (!token || !currentUser || currentUser.role !== "admin") return showPage("login");

    $("#loadingSpinner").show();

    $.ajax({
        url: API_EVENTS + "/joinable/" + currentUser.id,
        method: "GET",
        headers: { Authorization: "Bearer " + token },
        success: function (tabellaEventi) {
            let counter = 0;
            $("#tabellaEventiElimina").empty();

            tabellaEventi.forEach(function (event) {
                counter++;
                $("#tabellaEventiElimina").append(`
                    <tr>
                        <td>${counter}</td>
                        <td>${event.playDate}</td>
                        <td>${event.playTime}</td>
                        <td>${event.description}</td>
                        <td>${event.playingField.name}</td>
                        <td>${event.users.length}</td>
                        <td>
                            <button class="btn btn-danger btn-sm" onclick="deleteEvent(${event.id})">Elimina</button>
                        </td>
                    </tr>
                `);
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            showHttpError("Errore durante il caricamento degli eventi per l'eliminazione", jqXHR, textStatus, errorThrown);
        },
        complete: function () {
            $("#loadingSpinner").hide();
        }
    });
}

$('#btn-change-password').click(function () {
  $('#page-user').hide(); // nasconde la pagina user
  $('#change-password-form-container').show(); // mostra il form di cambio password
});

$('#cancel-change-password').click(function () {
  $('#change-password-form-container').hide(); // nasconde il form
  $('#page-user').show(); // ri-mostra la pagina user
})

// Nascondi il form di cambio password se l'utente annulla
$('#cancel-change-password').click(function () {
  $('#change-password-form-container').hide();
});

// Gestisci il submit del form di cambio password
$('#change-password-form').submit(function (e) {
  e.preventDefault();

  const oldPassword = $('#old-password').val();
  const newPassword = $('#new-password').val();
  const confirmPassword = $('#confirm-password').val();

  if (newPassword !== confirmPassword) {
    alert("Le nuove password non corrispondono.");
    return;
  }

 $.ajax({
   url: 'http://localhost:8080/giocacongigi/api/user/change-password',
   method: 'POST',
   contentType: 'application/json',
   data: JSON.stringify({
     currentPassword: oldPassword,
     newPassword: newPassword
   }),
   beforeSend: function(xhr) {
     var token = localStorage.getItem("token");  // Ottieni il token da storage
     if (!token) {
       alert("Token non trovato! Autenticazione richiesta.");
       return;  // Blocca la richiesta se non c'è il token
     }
     xhr.setRequestHeader("Authorization", "Bearer " + token);
   },
   success: function() {
     alert("Password cambiata con successo!");
     $('#change-password-form-container').hide();
     $('#page-user').show();
   },
   error: function(xhr) {
     alert("Errore durante il cambio password:\n" + xhr.responseText);
   }
 });
 });








