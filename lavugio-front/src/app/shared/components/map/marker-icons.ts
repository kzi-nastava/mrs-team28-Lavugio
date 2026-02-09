// src/app/map/marker-icons.ts
import * as L from 'leaflet';

const elongatedPinSvg = (color: string) => `
<svg width="24" height="32" viewBox="0 0 100 140"
     xmlns="http://www.w3.org/2000/svg">
  <path
    fill="${color}"
    d="
      M50 5
      C22 5 8 28 8 52
      C8 90 50 135 50 135
      C50 135 92 90 92 52
      C92 28 78 5 50 5
      Z
    "
  />
  <circle cx="50" cy="50" r="14" fill="#ffffff"/>
</svg>
`;

const pin = (color: string) =>
  L.divIcon({
    html: elongatedPinSvg(color),
    iconSize: [20, 28],    // smanjeno
    iconAnchor: [10, 28],  // proporcionalno smanjen
    className: ''
  });

/* ================= AUTO ================= */

const carSvg = (bodyColor: string) => `
<svg fill="${bodyColor}"
     width="20"
     height="20"
     viewBox="-1 0 19 19"
     xmlns="http://www.w3.org/2000/svg">
  <path d="M16.417 9.583A7.917 7.917 0 1 1 8.5 1.666
           a7.917 7.917 0 0 1 7.917 7.917zm-3.948-1.455
           -.758-1.955a.816.816 0 0 0-.726-.498H6.054
           a.816.816 0 0 0-.727.498L4.57 8.128
           a1.43 1.43 0 0 0-1.052 1.375v2.046
           a.318.318 0 0 0 .317.317h.496v1.147
           a.238.238 0 0 0 .238.237h.892
           a.238.238 0 0 0 .237-.237v-1.147h5.644
           v1.147a.238.238 0 0 0 .237.237h.892
           a.238.238 0 0 0 .238-.237v-1.147h.496
           a.318.318 0 0 0 .317-.317V9.503
           a1.43 1.43 0 0 0-1.052-1.375zm-7.445.582
           a.792.792 0 1 0 .792.792
           .792.792 0 0 0-.792-.792zm5.96-2.402
           a.192.192 0 0 1 .137.094l.65 1.676H5.267
           l.65-1.676a.192.192 0 0 1 .136-.094h4.93zm1.04
           2.402a.792.792 0 1 0 .792.792
           .792.792 0 0 0-.791-.792z"/>
</svg>
`;

const driverIcon = (color: string) =>
  L.divIcon({
    html: carSvg(color),
    iconSize: [18, 18],    // smanjeno
    iconAnchor: [9, 9],    // proporcionalno smanjen
    className: ''
  });

/* ================= EXPORT ================= */

export const MarkerIcons = {
  start: pin('#BC6C25'),
  end: pin('#606C38'),
  checkpoint: pin('#DDA15E'),

  driverAvailable: driverIcon('#606C38'),
  driverReserved: driverIcon('#DDA15E'),
  driverBusy: driverIcon('#BC6C25'),

  default: pin('#FEFAE0')
};
