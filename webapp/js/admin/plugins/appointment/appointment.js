const setNeWcolor = () => {
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
  if (!hex || hex === undefined || hex === '') {
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