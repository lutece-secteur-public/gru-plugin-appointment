var setNeWcolor = () => {
  const randomColor = Math.floor(Math.random()*16777215).toString(16);
  return "#" + randomColor;
}

var rgbToHex = (function () {
  var rx = /^rgb\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)$/i;

  function pad(num) {
      if (num.length === 1) {
          num = "0" + num;
      }

      return num;
  }

  return function (rgb, uppercase) {
      var rxArray = rgb.match(rx),
          hex;

      if (rxArray !== null) {
          hex = pad(parseInt(rxArray[1], 10).toString(16)) + pad(parseInt(rxArray[2], 10).toString(16)) + pad(parseInt(rxArray[3], 10).toString(16));

          if (uppercase === true) {
              hex = hex.toUpperCase();
          }

          return hex;
      }

      return;
  };
}());

function rgbToYIQ({r, g, b}) {
  return ((r * 299) + (g * 587) + (b * 114)) / 1000;
}

function hexToRgb(hex) {
  if ( hex === undefined || !hex || hex === '') {
    return undefined;
  }

  const result =
        /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);

  return result ? {
    r: parseInt(result[1], 16),
    g: parseInt(result[2], 16),
    b: parseInt(result[3], 16)
  } : undefined;
}

function contrast(colorHex, threshold = 128) {
  if (colorHex === undefined) {
    return '#000';
  }

  const rgb = hexToRgb(colorHex);

  if (rgb === undefined) {
    return '#000';
  }

  return rgbToYIQ(rgb) >= threshold ? '#000' : '#fff';
}

function setLabelComment( event, locale ){
  function toDateStr(d) {
    var dt = new Date(d);
    return dt.getFullYear() + '-' + String(dt.getMonth() + 1).padStart(2, '0') + '-' + String(dt.getDate()).padStart(2, '0');
}

  function formatShort(d, loc) {
    var dt = new Date(d);
    var weekday = dt.toLocaleDateString(loc, { weekday: 'short' });
    var day = String(dt.getDate()).padStart(2, '0');
    var month = String(dt.getMonth() + 1).padStart(2, '0');
    return weekday + ' ' + day + '/' + month;
  }
  var labelEvent = '';
  if ( toDateStr(event.start) === toDateStr(event.validity_end) ) {
    if ( event.start_time == '00:00' && event.end_time == '00:00' ) {
      labelEvent = 'Le ' + formatShort(event.start, locale);
    } else {
      labelEvent = 'Le ' + formatShort(event.start, locale) + ' ' + event.start_time + ' - ' + event.end_time;
    }
  } else {
    if ( event.start_time == '00:00' && event.end_time == '00:00' ) {
      labelEvent = 'Du ' + formatShort(event.start, locale) + ' au ' + formatShort(event.validity_end, locale);
    } else {
      labelEvent = 'Du ' + formatShort(event.start, locale) + ' ' + event.start_time + ' au ' + formatShort(event.validity_end, locale) + ' ' + event.end_time;
    }
  }
  return labelEvent;
}