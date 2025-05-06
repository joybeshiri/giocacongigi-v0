const API_AUTH   = "http://localhost:8080/giocacongigi/api/auth";
const API_USER   = "http://localhost:8080/giocacongigi/api/user";
const API_EVENTS = "http://localhost:8080/giocacongigi/api/events";

let currentUser = null;

function showPage(page) {
  $('.page').hide();
  $('#btn-logout').hide();
  $('#btn-profile').hide();
  $('#btn-home').hide();
  $('#btn-home-admin').hide();
  $('form').each(function() {
    this.reset();
  });

  // Nascondi il form di cambio password se visibile
  $('#page-change-password').hide();

  switch (page) {
    case "home":
      break;
    case "login":
      break;
    case "register":
      break;
    case "user":
      $('#btn-home').show();
      //visualizzaEventi();
      getNearbyEvents(100);
      $('#btn-logout').show();
      $('#btn-profile').show();
      break;
    case "admin":
      $('#btn-home-admin').show();
      $('#btn-logout').show();
      $('#btn-profile').show();
      break;
    case "create-event":
      $('#btn-home-admin').show();
      $('#btn-logout').show();
      caricaCampiDaGioco();
      $('#btn-profile').show();
      break;
    case "delete-event":
      $('#btn-home-admin').show();
      $('#btn-logout').show();
      visualizzaEventiPerEliminazione();
      $('#btn-profile').show();
       break;
    case "view-event":
      $('#btn-home-admin').show();
      visualizzaEventi("view");
      //getNearbyEvents(100);
      $('#btn-logout').show();
      $('#btn-profile').show();
      break;
    case "profile":
      $('#btn-home').show();
      $('#btn-logout').show();
      $('#btn-profile').show();
      break;
    case "change-password":
      $('#btn-home').show();
      $('#btn-logout').show();
      $('#btn-profile').show();
        break;
    case "edit-event":
      $('#btn-home-admin').show();
      $('#btn-logout').show();
      $('#btn-profile').show();
      break;
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

  $('#btn-login').click(function (event) { doClick(event, this.id); });
  $('#btn-register').click(function (event) { doClick(event, this.id); });
  $('#btn-logout').click(function (event) { doClick(event, this.id); });
  $('#btn-create-event').click(function (event) { doClick(event, this.id); });
  $('#btn-view-event').click(function (event) { doClick(event, this.id); });
  $('#btn-profile').click(function (event) { doClick(event, this.id); });
  $('#btn-change-password').click(function (event) { doClick(event, this.id); });
  $('#btn-edit-event').click(function (event) { doClick(event, this.id); });

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
  $('#btn-home').click(function (event) {
    event.preventDefault();
    showPage("user");
  });
  $('#cancel-change-password').click(function (event) {
    event.preventDefault();
    showPage("profile");
  });
  $('#btn-back-to-home').click(function (event) {
    event.preventDefault();
    showPage("home");
  });
  
  $('#btn-home-admin').click(function (event) {
    event.preventDefault();
    showPage("admin");
  });
  $('#btn-info').click(function (event) {
    event.preventDefault();
    showPage("info");
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
  $('#change-password-form-container').show();// mostra il form di cambio password
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
     $('#page-change-password').hide();
     $('#page-profile').show();
   },
   error: function(xhr) {
     alert("Errore durante il cambio password:\n" + xhr.responseText);
   }
 });
 });

 // Funzione principale per ottenere la posizione dell'utente e inviarla al backend
 function getNearbyEvents() {
     // Verifica che la geolocalizzazione sia supportata dal browser
     if (!navigator.geolocation) {
         console.warn("Geolocalizzazione non supportata dal browser. Uso posizione basata su IP.");
         getLocationFromIP(); // Usa il fallback basato su IP
         return;
     }

     // Richiesta per ottenere la posizione dell'utente tramite geolocalizzazione
     navigator.geolocation.getCurrentPosition(
         // Callback in caso di successo
         function (position) {
             const latitude = position.coords.latitude;
             const longitude = position.coords.longitude;
             console.log("Latitudine:", latitude);
             console.log("Longitudine:", longitude);

             // Invia la posizione al backend
             sendLocationToBackend(latitude, longitude);
         },
         // Callback in caso di errore
         function (error) {
             console.error("Errore nella geolocalizzazione:", error);
             alert("Impossibile ottenere la posizione tramite GPS. Uso la posizione basata su IP come fallback.");
             getLocationFromIP(); // Fallback su IP-API
         },
         {
             timeout: 15000, // Timeout di 15 secondi
             maximumAge: 0,  // Non utilizzare una posizione memorizzata in cache
             enableHighAccuracy: false // Usa GPS se disponibile (meno preciso senza alta precisione)
         }
     );
 }

 // Fallback: Funzione per ottenere la posizione basata su IP usando ip-api
 function getLocationFromIP() {
     fetch('http://ip-api.com/json/')
         .then((response) => {
             if (!response.ok) {
                 throw new Error("Errore nella chiamata all'API ip-api.");
             }
             return response.json();
         })
         .then((data) => {
             if (data.status === "success") {
                 const latitude = data.lat; // Latitudine restituita da ip-api
                 const longitude = data.lon; // Longitudine restituita da ip-api

                 console.log("Posizione basata su IP:", latitude, longitude);

                 // Invia la posizione al backend
                 sendLocationToBackend(latitude, longitude);
             } else {
                 console.error("Non è stato possibile ottenere la posizione basata su IP.");
                 alert("Non è stato possibile determinare la tua posizione.");
             }
         })
         .catch((error) => {
             console.error("Errore durante il recupero dalla geolocalizzazione IP:", error);
             alert("Errore nel recupero della posizione basata su IP.");
         });
 }

 // Funzione per inviare i dati di geolocalizzazione al backend
function sendLocationToBackend(latitude, longitude) {
    const maxDistance = 100; // Ad esempio, distanza massima in km
    const token = localStorage.getItem('authToken'); // Ottieni il token

    console.log("Invio al backend: Latitudine =", latitude, ", Longitudine =", longitude);

    // Aggiorna il percorso con il prefisso /giocacongigi
    const url = `/giocacongigi/api/events/events-nearby?userLat=${latitude}&userLon=${longitude}&maxDistance=${maxDistance}&user_id=${currentUser.id}`;

    fetch(url, {
        method: 'GET', // Metodo GET per ottenere i dati
        headers: {
            'Accept': 'application/json',
            'Authorization': `Bearer ${token}` // Invia il token al backend
        }
    })
    .then((response) => {
        console.log("Stato della risposta:", response.status);
        if (!response.ok) {
            throw new Error(`Errore nella risposta del backend: ${response.status}`);
        }
        return response.json();
    })
    .then((events) => {
           console.log("Eventi vicini ricevuti dal backend:", events);

           // Controllo aggiuntivo per verificare la struttura di 'events'
           if (!Array.isArray(events)) {
               console.error("La risposta dal backend non è un array.");
               return;
           }
           if (events.length === 0) {
               console.warn("Il backend non ha restituito eventi.");
           }

           // Visualizza gli eventi ricevuti nella tabella
           displayEvents(events, latitude, longitude);
       })
    .catch((error) => {
        console.error("Errore durante l'invio al backend:", error);
    });
}






 // Funzione per gestire gli errori di geolocalizzazione
 function handleGeolocationError(error) {
     switch (error.code) {
         case error.PERMISSION_DENIED:
             alert("L'utente ha negato i permessi per la geolocalizzazione.");
             break;
         case error.POSITION_UNAVAILABLE:
             alert("La posizione non è disponibile nel tuo dispositivo.");
             break;
         case error.TIMEOUT:
             alert("La richiesta per ottenere la posizione è scaduta.");
             break;
         default:
             alert("Si è verificato un errore sconosciuto durante il tentativo di geolocalizzazione.");
             break;
     }
 }

 // Funzione per mostrare gli eventi nella tabella
 function hideSpinner() {
     const spinner = document.getElementById("loadingSpinner");
     if (spinner) spinner.style.display = "none";
 }

function displayEvents(events) {
       console.log("Funzione displayEvents chiamata.");
       console.log("Dati degli eventi passati:", events);

       const eventsContainer = document.querySelector("#tabellaEventi");
       if (!eventsContainer) {
           console.error("Contenitore della tabella non trovato!");
           return;
       }

       // Svuota il contenitore prima di aggiungere le nuove righe
       eventsContainer.innerHTML = "";

       // Verifica se ci sono eventi
       if (!events || events.length === 0) {
           console.warn("Nessun evento trovato.");
           eventsContainer.innerHTML =
               '<tr><td colspan="8" class="text-center">Nessun evento disponibile.</td></tr>';
           return;
       }



      const now = new Date(); // Ottieni l'orario attuale

      events.forEach((event, index) => {
          // Combina playDate e playTime per ottenere la data e l'ora completa dell'evento
          const eventDateTime = new Date(`${event.playDate}T${event.playTime}`);

          // Verifica se l'evento è successivo ad ora
          if (eventDateTime > now) {
              console.log(`Elaboro l'evento futuro ${index + 1}:`, event);

              // Crea una riga della tabella
              const row = document.createElement("tr");


              let btn = "";
              let action = event.joinable ? "subscribe" : "unsubscribe";
              let btn_text = event.joinable ? "iscriviti" : "annulla iscrizione";
              btn = "<a href='#' onclick='subscribeOrUnsubscribe(" + event.id + ", " + currentUser.id + ", \"" + action + "\")' class='btn btn-primary btn-sm'>" + btn_text + "</a>";

              row.innerHTML = `
                  <td>${index + 1}</td>
                  <td>${event.playDate || "N/A"}</td>
                  <td>${event.playTime || "N/A"}</td>
                  <td>${event.description || "Nessuna Descrizione"}</td>
                  <td>${event.playingField?.name || "Campo Sconosciuto"} (${event.playingField?.description || "N/A"})</td>
                  <td>${event.users.length}</td>
                  <td>${btn}</td>
                  <td>${event.distance ? event.distance.toFixed(2) : "N/A"} km</td>
              `;

              // Aggiungi la riga alla tabella
              eventsContainer.appendChild(row);
          } else {
              console.log(`Evento ${index + 1} escluso (già passato):`, event);
          }
      });


       console.log("Tabella aggiornata con nuovi eventi.");
   }

   $(document).ready(function () {
     function cambiaSfondo() {
       const ora = new Date().getHours();
       console.log(ora);
       if (ora >= 7 && ora < 18) {
         $("#page-home").addClass("day");

       } else {
         $("#page-home").addClass("night");
       }
     }
     cambiaSfondo();
   });


   $(document).ready(function () {
     function cambiaSfondo() {
       const ora = new Date().getHours();
       console.log(ora);
       if (ora >= 7 && ora < 18) {
         $("#page-login").addClass("day");

       } else {
         $("#page-login").addClass("night");
       }
     }
     cambiaSfondo();
   });











