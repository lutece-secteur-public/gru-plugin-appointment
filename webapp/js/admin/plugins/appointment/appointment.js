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
  moment.locale( locale );
  var labelEvent='';
  if ( moment( moment(event.start).format('YYYY-MM-DD') ).isSame( moment( event.validity_end ).format('YYYY-MM-DD') ) ){
    if(  event.start_time == '00:00' &&  event.end_time == '00:00' ){
      labelEvent='Le ' +  moment(event.start).format('ddd DD/MM');
    } else {
      labelEvent='Le ' +  moment(event.start).format('ddd DD/MM') + ' ' + event.start_time + ' - ' + event.end_time;
    }
  } else {
    moment.locale("${locale}");
    if(  event.start_time == '00:00' &&  event.end_time == '00:00' ){
      labelEvent = 'Du ' +  moment(event.start).format('ddd DD/MM') + ' au '+ moment(event.validity_end).format('ddd DD/MM');
    } else {
      labelEvent = 'Du ' +  moment(event.start).format('ddd DD/MM') + ' ' + event.start_time + ' au '+ moment(event.validity_end).format('ddd DD/MM') + ' ' + event.end_time;
    }
  }
  return labelEvent
}