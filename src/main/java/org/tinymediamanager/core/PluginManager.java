package org.tinymediamanager.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.scraper.IKodiMetadataProvider;
import org.tinymediamanager.scraper.IMediaProvider;
import org.tinymediamanager.scraper.IMediaSubtitleProvider;
import org.tinymediamanager.scraper.IMovieArtworkProvider;
import org.tinymediamanager.scraper.IMovieMetadataProvider;
import org.tinymediamanager.scraper.IMovieTrailerProvider;
import org.tinymediamanager.scraper.ITvShowArtworkProvider;
import org.tinymediamanager.scraper.ITvShowMetadataProvider;
import org.tinymediamanager.scraper.MediaScraper;
import org.tinymediamanager.scraper.MediaScraper;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.GetPluginOption;
import net.xeoh.plugins.base.options.addpluginsfrom.OptionReportAfter;
import net.xeoh.plugins.base.options.getplugin.OptionPluginSelector;
import net.xeoh.plugins.base.options.getplugin.PluginSelector;
import net.xeoh.plugins.base.util.JSPFProperties;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.base.util.uri.ClassURI;

public class PluginManager {
  private final static Logger                        LOGGER = LoggerFactory.getLogger(PluginManager.class);
  private static net.xeoh.plugins.base.PluginManager pm;
  private static PluginManagerUtil                   pmu;
  private static PluginManager                       instance;

  public PluginManager() {
  }

  public synchronized static PluginManager getInstance() {
    if (instance == null) {
      JSPFProperties props = new JSPFProperties();
      props.setProperty(PluginManager.class, "cache.enabled", "true");
      props.setProperty(PluginManager.class, "cache.mode", "weak"); // optional
      props.setProperty(PluginManager.class, "cache.file", "jspf.cache");

      instance = new PluginManager();
      pm = PluginManagerFactory.createPluginManager(props);
      pmu = new PluginManagerUtil(pm);

      long start = System.currentTimeMillis();
      LOGGER.debug("loading inline plugins...");
      // pm.addPluginsFrom(ClassURI.CLASSPATH); // sloooow
      pm.addPluginsFrom(ClassURI.CLASSPATH("org.tinymediamanager.scraper.**"));
      // pm.addPluginsFrom(ClassURI.PLUGIN(AniDBMetadataProvider.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(FanartTvMetadataProvider.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(HDTrailersNet.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(ImdbMetadataProvider.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(MoviemeterMetadataProvider.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(OfdbMetadataProvider.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(OpensubtitlesMetadataProvider.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(TheSubDbMetadataProvider.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(TheTvDbMetadataProvider.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(TmdbMetadataProvider.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(TraktTv.class));
      // pm.addPluginsFrom(ClassURI.PLUGIN(ZelluloidMetadataProvider.class));
      long end = System.currentTimeMillis();
      LOGGER.debug("Done loading plugins - took " + (end - start) + " - " + Utils.MSECtoHHMMSS(end - start));

      // dedicated folder just for plugins
      LOGGER.debug("loading external plugins...");
      if (LOGGER.isTraceEnabled()) {
        pm.addPluginsFrom(new File("plugins/").toURI(), new OptionReportAfter());
      }
      else {
        pm.addPluginsFrom(new File("plugins/").toURI());
      }
    }
    return instance;
  }

  private Class ScraperToImplClass(MediaScraper scraper) {
    Class c = IMediaProvider.class;
    switch (scraper.getType()) {
      case MOVIE:
        c = MediaScraper.class;
        break;
      case TV_SHOW:
        c = ITvShowMetadataProvider.class;
        break;
      case MOVIE_ARTWORK:
        c = IMovieArtworkProvider.class;
        break;
      case TV_SHOW_ARTWORK:
        c = ITvShowArtworkProvider.class;
        break;
      case TRAILER:
        c = IMovieTrailerProvider.class;
        break;
      case SUBTITLE:
        c = IMediaSubtitleProvider.class;
        break;
      case ALBUM:
      case ARTIST:
      case LIBRARY:
      case MUSICVIDEO:
      default:
        LOGGER.warn("No implementing interface for scraper: " + scraper);
        break;
    }
    return c;
  }

  /**
   * Gets the plugin from a MediaScraper
   * 
   * @param scraper
   *          the TMM scraper
   * @return Plugin
   */
  public Plugin getPlugin(final MediaScraper scraper) {
    Class paramClass = ScraperToImplClass(scraper);
    // return pm.getPlugin(paramClass, new OptionCapabilities("id:" + scraper.getId()));
    PluginSelector<Plugin> selector = new PluginSelector<Plugin>() {
      @Override
      public boolean selectPlugin(Plugin paramT) {
        if (paramT instanceof IMediaProvider && ((IMediaProvider) paramT).getProviderInfo().getId().equals(scraper.getId())) {
          return true;
        }
        return false;
      }
    };
    return pm.getPlugin(paramClass, new OptionPluginSelector<Plugin>(selector));
  }

  /**
   * Gets the plugin implementing a desired interface and capabilities
   * 
   * @param paramClass
   *          plugin implementing the TMM interface IMedia...
   * @param paramVarArgs
   *          String to fetch desired capabilities like "id:tmdb.org"
   * @return Plugin
   */
  public Plugin getPlugin(Class paramClass, GetPluginOption... paramVarArgs) {
    return pm.getPlugin(paramClass, paramVarArgs);
  }

  /**
   * All plugins implementing the IMediaProvider
   */
  public List<IMediaProvider> getPlugins() {
    List<IMediaProvider> plugins = new ArrayList<>();
    for (Plugin p : pmu.getPlugins(IMediaProvider.class)) {
      plugins.add((IMediaProvider) p);
    }
    return plugins;
  }

  /**
   * All plugins implementing the IMediaMetadataProvider
   */
  public List<IMovieMetadataProvider> getMoviePlugins() {
    List<IMovieMetadataProvider> plugins = new ArrayList<>();
    for (Plugin p : pmu.getPlugins(IMovieMetadataProvider.class)) {
      plugins.add((IMovieMetadataProvider) p);
    }
    return plugins;
  }

  /**
   * All plugins implementing the IMovieArtworkProvider
   */
  public List<IMovieArtworkProvider> getMovieArtworkPlugins() {
    List<IMovieArtworkProvider> plugins = new ArrayList<>();
    for (Plugin p : pmu.getPlugins(IMovieArtworkProvider.class)) {
      plugins.add((IMovieArtworkProvider) p);
    }
    return plugins;
  }

  /**
   * All plugins implementing the ITvShowArtworkProvider
   */
  public List<ITvShowArtworkProvider> getTvShowArtworkPlugins() {
    List<ITvShowArtworkProvider> plugins = new ArrayList<>();
    for (Plugin p : pmu.getPlugins(ITvShowArtworkProvider.class)) {
      plugins.add((ITvShowArtworkProvider) p);
    }
    return plugins;
  }

  /**
   * All plugins implementing the IMediaTrailerProvider
   */
  public List<IMovieTrailerProvider> getTrailerPlugins() {
    List<IMovieTrailerProvider> plugins = new ArrayList<>();
    for (Plugin p : pmu.getPlugins(IMovieTrailerProvider.class)) {
      plugins.add((IMovieTrailerProvider) p);
    }
    return plugins;
  }

  /**
   * All plugins implementing the IMediaSubtitleProvider
   */
  public List<IMediaSubtitleProvider> getSubtitlePlugins() {
    List<IMediaSubtitleProvider> plugins = new ArrayList<>();
    for (Plugin p : pmu.getPlugins(IMediaSubtitleProvider.class)) {
      plugins.add((IMediaSubtitleProvider) p);
    }
    return plugins;
  }

  /**
   * All plugins implementing the ITvShowMetadataProvider
   */
  public List<ITvShowMetadataProvider> getTvShowPlugins() {
    List<ITvShowMetadataProvider> plugins = new ArrayList<>();
    for (Plugin p : pmu.getPlugins(ITvShowMetadataProvider.class)) {
      plugins.add((ITvShowMetadataProvider) p);
    }
    return plugins;
  }

  public List<IKodiMetadataProvider> getKodiPlugins() {
    List<IKodiMetadataProvider> plugins = new ArrayList<>();
    for (Plugin p : pmu.getPlugins(IKodiMetadataProvider.class)) {
      plugins.add((IKodiMetadataProvider) p);
    }
    return plugins;
  }

}
