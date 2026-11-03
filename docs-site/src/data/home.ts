// Content of the homepage sections: apps, sponsors and talks. Logo files live in
// src/assets/logos, and LogoWall.astro fails the build if one is missing.

export interface Logo {
  name: string;
  file: string;
  href: string;
}

const play = (id: string) => `https://play.google.com/store/apps/details?id=${id}`;

export const apps: Logo[] = [
  { name: 'Liberty Rider', file: 'liberty.webp', href: play('com.libertyrider.angel') },
  { name: 'Plum', file: 'plum.webp', href: play('com.withplum.app') },
  { name: 'TIER', file: 'tier.webp', href: play('com.tier.app') },
  { name: 'Shopmium', file: 'shopmium.webp', href: play('com.shopmium') },
  { name: 'Kakao T', file: 'kako.webp', href: play('com.kakao.taxi') },
  { name: 'Swile', file: 'swile.webp', href: play('hr.lunc.client') },
  { name: 'Curve', file: 'curve.webp', href: play('com.imaginecurve.curve.prd') },
  { name: 'Banque Populaire', file: 'bp.webp', href: play('fr.banquepopulaire.cyberplus') },
  { name: 'Banxo', file: 'ce.webp', href: play('com.caisseepargne.android.mobilebanking') },
  { name: 'CANAL+', file: 'canal.webp', href: play('com.canal.android.canal') },
  { name: 'KaraFun', file: 'karafun.webp', href: play('com.recisio.kfandroid') },
  { name: 'Currency Converter Calculator', file: 'ccc.webp', href: play('mustafaozhan.github.com.mycurrencies') },
  { name: 'Doximity', file: 'doximity.webp', href: play('com.doximity.doximitydroid') },
  { name: 'Zoe', file: 'zoe.webp', href: play('com.surgeapp.zoe') },
  { name: 'SURGE', file: 'surge.webp', href: play('com.lavendrapp.lavendr') },
  { name: 'Offline Music Player', file: 'offlinemusic.webp', href: play('com.nomad88.nomadmusic') },
  { name: 'Test Your Android', file: 'test.webp', href: play('hibernate.v2.testyourandroid') },
  { name: 'Markaz', file: 'markaz.png', href: play('com.markaz.app') },
];

export const sponsors: Logo[] = [
  { name: 'JetBrains', file: 'jetbrains.png', href: 'https://www.jetbrains.com' },
  { name: 'sipgate', file: 'sipgate.png', href: 'https://sipgate.co.uk' },
];

export const talks = [
  { id: 'GtrLY4vfZlw', title: 'Android Architecture Design with Koin, by Arnaud Giuliani' },
  { id: 'mt9yoWScgb8', title: 'Dependency injection from zero to hero with Koin, by Arnaud Giuliani' },
];
