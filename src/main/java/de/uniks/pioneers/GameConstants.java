package de.uniks.pioneers;

import javafx.scene.paint.Color;

public class GameConstants {
    //Map
    public static final double MAP_X = 18;
    public static final double MAP_Y = 15;
    public static final double MAP_WIDTH = 715;
    public static final double MAP_HEIGHT = 631;

    public static final double MAP_PADDING_X = 120;
    public static final double MAP_PADDING_Y = 120;
    public static final double eulerC = 0.57721566490153286060651209008240243104215933593992;

    // move actions
    public static final String FOUNDING_ROLL = "founding-roll";
    public static final String FOUNDING_SETTLEMENT_1 = "founding-settlement-1";
    public static final String FOUNDING_SETTLEMENT_2 = "founding-settlement-2";
    public static final String FOUNDING_ROAD_1 = "founding-road-1";
    public static final String FOUNDING_ROAD_2 = "founding-road-2";
    public static final String ROLL = "roll";
    public static final String BUILD = "build";
    public static final String OFFER = "offer";
    public static final String ACCEPT = "accept";


    public static final String DROP = "drop";
    public static final String ROB = "rob";

    // action strings
    public static final String ROLL_DICE = "roll the dice";
    public static final String PLACE_SETTLEMENT = "place settlement";
    public static final String PLACE_ROAD = "place road";

    // svg contents
    public static final String SETTLEMENT_SVG = "M 25.359375 7.1289062 C 25.234375 7.1289062 25.007812 7.2187496 24.863281 7.3359375 C 24.35547 7.7343741 24.66797 8.59375 25.332031 8.59375 C 25.664062 8.59375 26.074219 8.1953117 26.074219 7.859375 C 26.074219 7.5390623 25.671875 7.1289063 25.359375 7.1289062 z M 13.964844 7.421875 C 13.828125 7.421875 13.613281 7.5078116 13.496094 7.6171875 C 13.378906 7.7343744 13.28125 7.8984381 13.28125 8 C 13.28125 8.1171879 13.171875 8.2226566 12.976562 8.2890625 C 12.402345 8.4843753 12.246095 8.9531255 12.597656 9.40625 C 12.703125 9.5390619 12.890625 9.667969 13.015625 9.6875 C 13.15625 9.707031 13.242188 9.7968746 13.261719 9.9296875 C 13.300779 10.187499 13.710938 10.546875 13.964844 10.546875 C 14.218749 10.546875 14.628906 10.187499 14.667969 9.9296875 C 14.687499 9.7968746 14.773439 9.707031 14.914062 9.6875 C 15.148438 9.648438 15.527344 9.2382803 15.527344 9.0234375 C 15.527344 8.7187498 15.281249 8.3984382 14.980469 8.3007812 C 14.757812 8.2226563 14.648437 8.1250004 14.648438 8.0078125 C 14.648438 7.7421888 14.265624 7.421875 13.964844 7.421875 z M 37.640625 8.28125 C 37.540589 8.2869873 37.43457 8.3125 37.324219 8.359375 C 37.101562 8.4453119 36.992188 8.5742194 36.953125 8.7695312 C 36.914065 8.976562 36.816406 9.0703121 36.59375 9.140625 C 35.945313 9.3359378 35.945313 10.148438 36.59375 10.449219 C 36.765625 10.527349 36.914062 10.664062 36.914062 10.75 C 36.914062 11.007812 37.343751 11.328125 37.664062 11.328125 C 37.976562 11.328125 38.378906 10.968749 38.378906 10.683594 C 38.378906 10.605464 38.437496 10.546875 38.515625 10.546875 C 38.796875 10.546875 39.160156 10.148437 39.160156 9.8242188 C 39.160156 9.4921881 38.828125 9.0820313 38.5625 9.0820312 C 38.48437 9.0820313 38.359375 8.9374998 38.300781 8.75 C 38.183594 8.4277343 37.940734 8.264038 37.640625 8.28125 z M 20.578125 8.7890625 C 20.449219 8.7890625 20.226562 8.8749996 20.078125 8.9921875 C 19.570313 9.3945301 19.882813 10.253906 20.546875 10.253906 C 20.878906 10.253906 21.289062 9.8515612 21.289062 9.5234375 C 21.289062 9.1992188 20.890625 8.7890625 20.578125 8.7890625 z M 31.322266 10.939453 C 31.120606 10.942383 30.925781 11.001953 30.828125 11.113281 C 30.566406 11.40625 30.617192 11.960939 30.917969 12.195312 C 31.0625 12.3125 31.25 12.402344 31.347656 12.402344 C 31.445316 12.402344 31.632812 12.312504 31.777344 12.195312 C 32.089843 11.953126 32.140619 11.359375 31.855469 11.101562 C 31.732422 10.990235 31.523926 10.936523 31.322266 10.939453 z M 4.8613281 13.771484 C 4.6601565 13.773437 4.4648434 13.832032 4.3671875 13.945312 C 4.1015618 14.238281 4.1484383 14.796875 4.453125 15.03125 C 4.6015619 15.148438 4.7851566 15.234375 4.8828125 15.234375 C 4.9804694 15.234375 5.1640621 15.148435 5.3125 15.03125 C 5.6249997 14.785156 5.6718747 14.1875 5.390625 13.9375 C 5.2695316 13.824219 5.0624998 13.769531 4.8613281 13.771484 z M 21.419922 14.519531 C 20.553512 14.512922 19.698594 14.535207 19.269531 14.589844 C 15.94385 15.013333 13.079376 16.02321 10.568359 17.658203 C 9.8762375 18.108864 9.8649557 18.129334 9.9160156 18.955078 C 9.979255 19.977783 10.171367 21.371833 10.339844 22.035156 C 10.676086 23.359004 11.111587 23.840091 11.617188 23.447266 C 11.866435 23.253614 11.933986 23.171234 12.644531 22.166016 C 13.77093 20.572483 14.414749 19.879544 15.072266 19.556641 C 15.400165 19.395617 15.442166 19.390625 16.40625 19.390625 L 17.400391 19.390625 L 17.914062 19.638672 C 18.553418 19.947601 19.057898 20.326936 19.576172 20.886719 C 20.437678 21.817226 21.239306 23.010867 22.480469 25.210938 C 23.778516 27.511839 24.370976 28.475751 24.693359 28.808594 C 25.049664 29.176457 25.231746 29.215292 25.626953 29.013672 C 26.146476 28.748631 26.498679 28.112411 27.017578 26.507812 C 28.040976 23.343144 28.319818 22.651908 28.958984 21.677734 C 29.886359 20.264291 30.921707 19.356732 32.433594 18.632812 C 32.773611 18.470006 33.052734 18.32599 33.052734 18.3125 C 33.052734 18.263809 31.983317 17.559394 31.398438 17.222656 C 28.780646 15.715491 26.689996 15.015465 23.642578 14.623047 C 23.164548 14.561487 22.286332 14.52614 21.419922 14.519531 z M 40.908203 15.332031 C 40.724526 15.341044 40.53955 15.417485 40.429688 15.527344 C 39.804688 16.152343 40.683595 17.15625 41.359375 16.570312 C 41.882811 16.125001 41.621094 15.332031 40.945312 15.332031 C 40.933106 15.332031 40.920448 15.33143 40.908203 15.332031 z M 24.201172 16.894531 C 24.244914 16.895139 24.290748 16.89681 24.339844 16.902344 C 24.648926 16.937182 24.816958 17.063683 24.960938 17.369141 C 25.023265 17.501371 25.143116 17.618554 25.28125 17.683594 C 25.559396 17.814564 25.720275 18.052871 25.763672 18.302734 C 25.686237 18.024509 25.367722 17.675781 25.15625 17.675781 C 25.07031 17.675781 24.980469 17.570311 24.941406 17.429688 C 24.8125 16.914064 24.101561 16.71875 23.730469 17.089844 C 23.625 17.195312 23.535156 17.363281 23.535156 17.460938 C 23.535156 17.570311 23.417969 17.675781 23.203125 17.753906 C 22.531251 18 22.531251 18.886719 23.203125 19.082031 C 23.417969 19.148441 23.523438 19.257812 23.59375 19.472656 C 23.676416 19.753723 23.867318 19.914358 24.083984 19.955078 C 23.988883 19.94262 23.900968 19.91469 23.835938 19.869141 C 23.7616 19.817073 23.634164 19.643108 23.552734 19.482422 C 23.438988 19.257954 23.355283 19.174534 23.193359 19.121094 C 23.077447 19.08284 22.911809 18.965417 22.824219 18.861328 C 22.686369 18.697459 22.668339 18.631483 22.693359 18.371094 C 22.723971 18.05251 22.864414 17.877667 23.261719 17.664062 C 23.367158 17.607375 23.499192 17.453949 23.576172 17.294922 C 23.714312 17.009566 23.894976 16.890276 24.201172 16.894531 z M 25.771484 18.505859 C 25.749358 18.776011 25.591301 19.031599 25.292969 19.15625 C 25.180039 19.203435 25.025699 19.344179 24.943359 19.474609 C 24.862437 19.602798 24.724366 19.765458 24.634766 19.835938 C 24.55128 19.901607 24.42923 19.940382 24.302734 19.955078 C 24.534782 19.916239 24.765625 19.751465 24.902344 19.460938 C 24.980474 19.289062 25.117188 19.140625 25.203125 19.140625 C 25.415527 19.140625 25.716683 18.794694 25.771484 18.505859 z M 34.404297 19.351562 L 33.517578 19.804688 C 32.366505 20.393444 31.898978 20.702203 31.330078 21.242188 L 30.865234 21.683594 L 31.521484 21.716797 C 31.882576 21.735127 33.189801 21.755119 34.427734 21.761719 L 36.679688 21.773438 L 36.542969 21.578125 C 36.468145 21.470315 35.956726 20.925421 35.40625 20.367188 L 34.404297 19.351562 z M 8.34375 19.365234 C 8.2346568 19.394601 6.03125 21.621662 6.03125 21.705078 C 6.03125 21.753469 6.4142516 21.773438 7.359375 21.773438 L 8.6875 21.773438 L 8.65625 21.636719 C 8.6390529 21.562081 8.5696321 21.028286 8.5019531 20.449219 C 8.4342742 19.870152 8.3645332 19.382114 8.3476562 19.365234 C 8.3469975 19.364576 8.3454816 19.364768 8.34375 19.365234 z M 16.349609 20.832031 C 16.254347 20.833766 16.161538 20.84374 16.074219 20.863281 C 15.741089 20.937832 15.211126 21.276794 14.902344 21.613281 L 14.755859 21.773438 L 15.927734 21.773438 C 16.572087 21.773268 17.361764 21.755685 17.683594 21.734375 L 18.269531 21.695312 L 18.046875 21.517578 C 17.480575 21.067414 16.864027 20.822662 16.349609 20.832031 z M 45.033203 21.580078 C 44.868766 21.580464 44.713135 21.632813 44.609375 21.75 C 44.523435 21.84375 44.433594 22.011719 44.394531 22.117188 C 44.367191 22.226561 44.210938 22.375 44.042969 22.429688 C 43.429688 22.664061 43.417966 23.445312 44.015625 23.730469 C 44.1875 23.808599 44.367188 23.964845 44.394531 24.070312 C 44.5 24.421875 44.746094 24.609375 45.085938 24.609375 C 45.449218 24.609375 45.800781 24.328124 45.800781 24.023438 C 45.800781 23.898438 45.867191 23.828125 45.984375 23.828125 C 46.085938 23.828125 46.269531 23.749995 46.398438 23.640625 C 46.867187 23.273438 46.570311 22.363281 45.984375 22.363281 C 45.867188 22.363281 45.800781 22.296871 45.800781 22.167969 C 45.800781 21.826904 45.394965 21.579228 45.033203 21.580078 z M 29.820312 23.271484 L 29.662109 23.591797 C 29.303927 24.326876 28.602493 26.456009 28.689453 26.542969 C 28.71792 26.571439 29.052609 26.602631 29.433594 26.613281 C 29.814577 26.623931 30.773744 26.639384 31.564453 26.646484 L 33.001953 26.658203 L 32.958984 25.015625 C 32.935344 24.111676 32.903792 23.358387 32.888672 23.341797 C 32.873557 23.325207 32.176238 23.302156 31.339844 23.291016 L 29.820312 23.271484 z M 19.572266 23.279297 L 18.355469 23.294922 C 17.686416 23.303592 17.117797 23.333305 17.091797 23.359375 C 17.065807 23.385445 17.033531 24.138736 17.019531 25.033203 L 16.994141 26.658203 L 19.263672 26.658203 L 21.535156 26.658203 L 21.417969 26.431641 C 21.298901 26.199955 20.064941 24.082728 19.748047 23.566406 L 19.572266 23.279297 z M 4.8300781 23.28125 L 4.4335938 23.949219 C 3.9970916 24.684579 3.1716429 26.250425 3.109375 26.460938 C 3.0702984 26.59304 3.0779195 26.594381 3.78125 26.613281 C 4.1729226 26.623801 5.0157269 26.639384 5.6542969 26.646484 L 6.8164062 26.658203 L 6.8164062 24.970703 L 6.8164062 23.28125 L 5.8222656 23.28125 L 4.8300781 23.28125 z M 8.3242188 23.28125 L 8.3242188 24.970703 L 8.3242188 26.658203 L 11.912109 26.658203 L 15.5 26.658203 L 15.5 24.970703 L 15.5 23.28125 L 14.640625 23.283203 L 13.78125 23.287109 L 13.419922 23.773438 C 12.780825 24.632709 12.298274 24.980356 11.582031 25.095703 C 11.148367 25.165543 10.683567 25.099342 10.324219 24.916016 C 10.044985 24.773562 9.5687579 24.261239 9.3964844 23.917969 C 9.329023 23.783546 9.207783 23.584372 9.1269531 23.476562 C 8.992608 23.297374 8.9527786 23.28125 8.6523438 23.28125 L 8.3242188 23.28125 z M 34.5 23.28125 L 34.5 24.970703 L 34.5 26.658203 L 35.992188 26.650391 C 36.813207 26.646251 37.968835 26.626209 38.560547 26.605469 L 39.636719 26.568359 L 39.162109 25.632812 C 38.901615 25.118639 38.50181 24.380011 38.271484 23.990234 L 37.851562 23.28125 L 36.175781 23.28125 L 34.5 23.28125 z M 4.1015625 28.166016 L 4.1015625 29.855469 L 4.1015625 31.544922 L 7.6894531 31.544922 L 11.279297 31.544922 L 11.279297 29.855469 L 11.279297 28.166016 L 7.6894531 28.166016 L 4.1015625 28.166016 z M 21.591797 28.166016 L 21.591797 29.855469 L 21.591797 31.544922 L 25.181641 31.544922 L 28.769531 31.544922 L 28.769531 29.855469 L 28.769531 28.166016 L 28.476562 28.166016 C 28.128923 28.166016 28.100352 28.195534 27.773438 28.859375 C 27.597399 29.216843 27.417822 29.459616 27.082031 29.796875 C 26.571107 30.310033 26.26767 30.480768 25.625 30.615234 C 24.845729 30.778281 24.351236 30.625388 23.730469 30.029297 C 23.51664 29.823967 23.174736 29.409077 22.970703 29.107422 C 22.394246 28.255152 22.294835 28.166016 21.912109 28.166016 L 21.591797 28.166016 z M 30.277344 28.166016 L 30.277344 29.855469 L 30.277344 31.544922 L 31.912109 31.544922 L 33.546875 31.544922 L 34.007812 31.222656 C 35.090293 30.467992 35.747201 30.218735 36.775391 30.171875 L 37.455078 30.140625 L 37.455078 29.152344 L 37.455078 28.166016 L 33.865234 28.166016 L 30.277344 28.166016 z M 39.023438 28.166016 L 39.023438 29.162109 L 39.023438 30.15625 L 39.871094 30.15625 C 40.65026 30.15625 40.716743 30.147711 40.689453 30.050781 C 40.673113 29.992729 40.642384 29.850479 40.621094 29.734375 C 40.599806 29.618271 40.499508 29.218894 40.398438 28.845703 L 40.214844 28.166016 L 39.619141 28.166016 L 39.023438 28.166016 z M 14.484375 28.173828 C 13.830456 28.176603 13.348772 28.184288 13.224609 28.197266 L 12.847656 28.236328 L 12.847656 29.890625 L 12.847656 31.544922 L 16.474609 31.544922 L 20.101562 31.544922 L 20.070312 29.914062 C 20.053143 29.017098 20.019634 28.263804 19.996094 28.240234 C 19.954614 28.198707 16.446132 28.165504 14.484375 28.173828 z M 2.4980469 28.257812 C 2.480698 28.256816 2.462243 28.294975 2.4257812 28.375 C 2.3170583 28.613621 2.0143423 29.797619 1.8710938 30.548828 C 1.8015115 30.913726 1.7323849 31.27103 1.7167969 31.34375 C 1.6916479 31.461086 1.7176094 31.4795 1.9453125 31.5 C 2.0864469 31.51271 2.290627 31.527403 2.3984375 31.533203 L 2.59375 31.544922 L 2.5898438 29.990234 C 2.5875118 29.136043 2.5650996 28.387372 2.5410156 28.326172 C 2.5238807 28.282635 2.5115404 28.258588 2.4980469 28.257812 z M 44.560547 31.646484 L 44.902344 32.123047 C 45.345345 32.738238 45.51554 33.076974 45.630859 33.574219 C 45.748792 34.082718 45.832838 36.029755 45.835938 38.330078 C 45.837217 39.29208 45.857406 40.384622 45.878906 40.757812 L 45.917969 41.435547 L 47.107422 41.435547 C 48.141173 41.435547 48.306976 41.422727 48.378906 41.335938 C 48.446976 41.253813 48.462891 40.613506 48.462891 37.732422 L 48.462891 34.228516 L 48.166016 33.662109 C 47.997029 33.339835 47.725418 32.940426 47.533203 32.732422 C 46.88791 32.034123 46.268743 31.75602 45.208984 31.6875 L 44.560547 31.646484 z M 38.376953 31.671875 C 36.576107 31.669381 36.2039 31.732042 35.646484 32.005859 C 35.018564 32.314312 34.354051 32.981113 34.054688 33.603516 C 33.746788 34.243658 33.717997 34.654885 33.716797 38.277344 C 33.715867 41.03509 33.725257 41.365708 33.810547 41.398438 C 33.863019 41.418577 35.31023 41.435547 37.027344 41.435547 L 40.150391 41.435547 L 40.1875 40.488281 C 40.228628 39.435148 40.26317 39.336756 40.664062 39.117188 C 40.871793 39.003431 40.88337 39.002923 41.162109 39.109375 C 41.574058 39.266699 41.686899 39.542829 41.724609 40.5 C 41.740309 40.898404 41.775378 41.269692 41.804688 41.326172 C 41.84411 41.402127 41.955974 41.435342 42.234375 41.451172 C 42.441379 41.462942 42.997996 41.477975 43.470703 41.484375 L 44.330078 41.496094 L 44.326172 38.857422 C 44.321418 35.400033 44.269033 34.278203 44.091797 33.837891 C 43.838639 33.208958 43.257312 32.449219 43.029297 32.449219 C 42.777339 32.449219 42.132905 33.32007 41.923828 33.941406 C 41.829367 34.222132 41.780149 34.596256 41.730469 35.417969 C 41.658785 36.60374 41.616804 36.755972 41.3125 36.972656 C 41.046168 37.162299 40.771168 37.142765 40.447266 36.910156 L 40.175781 36.716797 L 40.207031 35.744141 C 40.26806 33.76363 40.387153 33.258296 41.017578 32.304688 L 41.419922 31.695312 L 39.242188 31.677734 C 38.921931 31.675178 38.634217 31.672231 38.376953 31.671875 z M 1.5078125 33.052734 L 1.5078125 34.710938 L 1.5078125 36.369141 L 4.1621094 36.369141 L 6.8164062 36.369141 L 6.8164062 34.710938 L 6.8164062 33.052734 L 4.1621094 33.052734 L 1.5078125 33.052734 z M 8.3242188 33.052734 L 8.3242188 34.710938 L 8.3242188 36.369141 L 11.912109 36.369141 L 15.5 36.369141 L 15.5 34.710938 L 15.5 33.052734 L 11.912109 33.052734 L 8.3242188 33.052734 z M 17.007812 33.052734 L 17.007812 34.710938 L 17.007812 36.369141 L 20.597656 36.369141 L 24.185547 36.369141 L 24.185547 34.710938 L 24.185547 33.052734 L 20.597656 33.052734 L 17.007812 33.052734 z M 25.814453 33.052734 L 25.814453 34.710938 L 25.814453 36.369141 L 29.011719 36.369141 L 32.207031 36.369141 L 32.207031 35.529297 C 32.207031 34.593994 32.285661 33.902898 32.443359 33.433594 C 32.547035 33.125045 32.547196 33.121021 32.423828 33.087891 C 32.355121 33.069441 30.83966 33.053187 29.056641 33.052734 L 25.814453 33.052734 z M 1.5 37.9375 L 1.5195312 39.585938 C 1.5395799 41.361638 1.5475377 41.405417 1.859375 41.453125 C 1.9481717 41.466715 2.1490714 41.481328 2.3066406 41.486328 L 2.59375 41.496094 L 2.59375 39.716797 L 2.59375 37.9375 L 2.046875 37.9375 L 1.5 37.9375 z M 4.1621094 37.9375 L 4.1621094 39.685547 L 4.1621094 41.435547 L 7.7207031 41.435547 L 11.279297 41.435547 L 11.279297 39.685547 L 11.279297 37.9375 L 7.7207031 37.9375 L 4.1621094 37.9375 z M 12.847656 37.9375 L 12.847656 39.685547 L 12.847656 41.435547 L 16.425781 41.435547 L 20.005859 41.435547 L 20.044922 40.757812 C 20.066422 40.384622 20.083803 39.596624 20.083984 39.007812 L 20.083984 37.9375 L 16.464844 37.9375 L 12.847656 37.9375 z M 21.591797 37.9375 L 21.591797 39.710938 L 21.591797 41.486328 L 25.134766 41.453125 C 27.083648 41.434975 28.690468 41.409434 28.705078 41.396484 C 28.719688 41.38353 28.743283 40.598924 28.757812 39.654297 L 28.785156 37.9375 L 25.189453 37.9375 L 21.591797 37.9375 z M 30.277344 37.9375 L 30.277344 39.71875 L 30.277344 41.498047 L 31.197266 41.458984 C 31.703145 41.437064 32.1205 41.415809 32.125 41.412109 C 32.12951 41.408386 32.153574 40.625583 32.177734 39.671875 L 32.220703 37.9375 L 31.25 37.9375 L 30.277344 37.9375 z";
    public static final String CITY_SVG = "M 29.484375 20.734375 C 29.34375 20.710938 29.210938 20.910156 28.859375 21.605469 C 28.472656 22.375 28.183594 23.179688 28.035156 23.910156 C 27.878906 24.679688 27.886719 26.816406 28.046875 27.574219 C 28.171875 28.164062 28.519531 29.121094 28.828125 29.738281 C 29.6875 31.441406 31.3125 33.035156 33.03125 33.859375 C 33.703125 34.179688 34.796875 34.546875 35.34375 34.628906 C 35.980469 34.722656 38.394531 34.664062 38.933594 34.542969 C 39.847656 34.332031 41.066406 33.832031 41.882812 33.332031 L 42.101562 33.195312 L 35.886719 27.011719 C 32.464844 23.609375 29.613281 20.792969 29.542969 20.753906 C 29.523438 20.742188 29.503906 20.738281 29.484375 20.734375 Z M 41.621094 21.054688 C 41.566406 21 36.835938 23.144531 36.835938 23.222656 C 36.835938 23.25 37.425781 23.839844 38.148438 24.53125 L 39.460938 25.792969 L 39.714844 25.296875 C 40.25 24.246094 41.664062 21.097656 41.621094 21.054688 Z M 30.6875 36.425781 C 30.675781 36.421875 30.667969 36.421875 30.664062 36.425781 C 30.597656 36.496094 29.269531 41.152344 28.632812 43.542969 L 28.414062 44.371094 L 29.65625 44.390625 C 32.078125 44.425781 39.144531 44.421875 39.59375 44.382812 L 40.046875 44.339844 L 38.976562 41.234375 C 38.386719 39.523438 37.890625 38.113281 37.871094 38.09375 C 37.851562 38.074219 37.3125 38.03125 36.667969 37.992188 C 34.621094 37.878906 32.9375 37.472656 31.429688 36.734375 C 31.082031 36.5625 30.777344 36.4375 30.6875 36.425781 Z M 34.304688 47.679688 C 24.875 47.679688 15.441406 47.726562 15.230469 47.820312 C 15.167969 47.84375 15.097656 47.914062 15.070312 47.972656 C 15.042969 48.035156 15.007812 48.699219 14.996094 49.453125 L 14.96875 50.824219 L 53.644531 50.824219 L 53.617188 49.453125 C 53.605469 48.699219 53.570312 48.035156 53.542969 47.972656 C 53.515625 47.914062 53.445312 47.84375 53.382812 47.820312 C 53.171875 47.726562 43.738281 47.679688 34.304688 47.679688 Z M 18.742188 54.113281 L 18.742188 68.042969 L 26.1875 68.042969 L 26.214844 64.449219 C 26.234375 61.957031 26.269531 60.78125 26.320312 60.621094 C 26.441406 60.238281 26.746094 59.882812 27.085938 59.714844 L 27.398438 59.5625 L 34.015625 59.542969 C 38.152344 59.53125 40.785156 59.550781 41.042969 59.589844 C 41.542969 59.667969 41.902344 59.894531 42.128906 60.28125 C 42.371094 60.691406 42.410156 61.320312 42.410156 64.898438 L 42.414062 68.042969 L 49.871094 68.042969 L 49.871094 54.113281 Z M 29.507812 62.753906 L 29.507812 68.042969 L 32.683594 68.042969 L 32.683594 62.753906 Z M 35.925781 62.753906 L 35.925781 68.042969 L 39.105469 68.042969 L 39.105469 62.753906 Z M 35.925781 62.753906";
    public static final double HOUSE_WIDTH = 25;
    public static final double HOUSE_HEIGHT = 28;
    public static final double CITY_WIDTH = 35;
    public static final double CITY_HEIGHT = 45;

    public static final double BUILDING_SCALING = 84; //The bigger the value, the smaller the buildings

    public static final Color RED = Color.RED;

    public static final  Color STANDARD_COLOR = Color.rgb(16, 78, 139);

    public static final Color HOVER_COLOR = Color.rgb(30, 144, 255);
    public static final Color BLUE = Color.BLUE;

    public static final Color GREEN = Color.GREEN;

    public static final Integer ROBBER_NUMBER = 7;

    public static final String INGAME_SCREEN_TITLE = "Pioneers ";
    public static final String SETTLEMENT = "settlement";
    public static final String CITY = "city";
    public static final String ROAD = "road";
    public static final String BANK_ID = "684072366f72202b72406465";

    //Robber States
    public static final int ROBBER_DISCARD = 1;
    public static final int ROBBER_MOVE = 2;
    public static final int ROBBER_STEAL = 3;
    public static final int ROBBER_FINISHED = 4;

    //Audio constants
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    public static final String SPEECH_ROLL_DICE = "rolldice";
    public static final String SPEECH_PLACE_IGLOO = "placeigloo";
    public static final String SPEECH_PLACE_STREET = "placestreet";
    public static final String SPEECH_BUILD = "build";
    public static final String SPEECH_DROP_RESOURCES = "dropresources";
    public static final String SPEECH_MOVE_ROBBER = "moverobber";
    public static final String SPEECH_STEAL = "steal";
}
