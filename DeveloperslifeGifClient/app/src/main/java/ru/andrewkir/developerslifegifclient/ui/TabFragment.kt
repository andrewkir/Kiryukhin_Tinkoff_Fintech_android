package ru.andrewkir.developerslifegifclient.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ru.andrewkir.developerslifegifclient.R
import ru.andrewkir.developerslifegifclient.data.api.ApiBuilder
import ru.andrewkir.developerslifegifclient.data.api.PostsApi
import ru.andrewkir.developerslifegifclient.databinding.FragmentTabBinding
import ru.andrewkir.developerslifegifclient.utils.SectionsEnum
import ru.andrewkir.developerslifegifclient.utils.ViewModelFactory


class TabFragment : Fragment() {

    private lateinit var binding: FragmentTabBinding

    private lateinit var currentSection: SectionsEnum
    private lateinit var viewModel: PostsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        processArguments()
        setupViewModel()

        observePost()
        observeError()
        observeLoading()
        observeButtonsVisibility()

        setupButtons()
    }

    private fun processArguments() {
        var pageNumber = 0
        arguments?.run {
            pageNumber = getInt(PAGE_NUMBER, 0)
        }
        currentSection = SectionsEnum.values()[pageNumber]
    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(this, ViewModelFactory(PostsApi(ApiBuilder.apiService))).get(
                PostsViewModel::class.java
            )
        viewModel.init(currentSection)
    }

    private fun observePost() {
        context?.let {
            viewModel.postLiveData.observe(viewLifecycleOwner, { post ->
                binding.mainLayout.visibility = View.VISIBLE
                binding.networkErrorLayout.visibility = View.GONE

                if (post == null) {
                    showErrorPicture()
                    binding.descriptionTextView.text = getString(R.string.error_empty_posts)
                } else post.run {
                    binding.loadingBar.visibility = View.VISIBLE
                    Glide.with(it)
                        .asGif()
                        .load(
                            gifURL?.replace(
                                "http://",
                                "https://"
                            )
                        )
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<GifDrawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<GifDrawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Toast.makeText(
                                    context,
                                    getString(R.string.error_gif_loading),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return false
                            }

                            override fun onResourceReady(
                                resource: GifDrawable?,
                                model: Any?,
                                target: Target<GifDrawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.loadingBar.visibility = View.GONE
                                return false
                            }
                        })
                        .override(300)
                        .transition(withCrossFade())
                        .centerCrop()
                        .into(binding.gifHolder)

                    //Смена текста описания с анимацией
                    binding.descriptionTextView.startAnimation(
                        AnimationUtils.loadAnimation(
                            context, android.R.anim.fade_in
                        )
                    )
                    binding.descriptionTextView.text = post.description

                    binding.gifHolder.setOnClickListener {
                        val url = "https://developerslife.ru/${post.id}"
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
            })
        }
    }

    private fun observeError() {
        binding.loadingBar.visibility = View.GONE
        viewModel.errorResponse.observe(viewLifecycleOwner, { error ->
            if (error != null) {
                showErrorPicture()
                if (error.isNetworkFailure) {
                    binding.mainLayout.visibility = View.GONE
                    binding.networkErrorLayout.visibility = View.VISIBLE
                } else {
                    Toast.makeText(context, error.body, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun observeButtonsVisibility() {
        viewModel.backButtonVisibility.observe(viewLifecycleOwner, {
            binding.backButton.isClickable = it
        })
        viewModel.forwardButtonVisibility.observe(viewLifecycleOwner, {
            binding.forwardButton.isClickable = it
        })
    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner, {
            it?.run {
                binding.loadingBar.visibility = if (this) View.VISIBLE else View.GONE
                binding.errorLoadingBar.visibility = binding.loadingBar.visibility

                binding.forwardButton.isClickable = !it
            }
        })
    }

    private fun showErrorPicture() {
        context?.let {
            Glide.with(it)
                .load(R.drawable.ic_hide_image)
                .transition(withCrossFade())
                .into(binding.gifHolder)
        }
    }

    private fun setupButtons() {
        binding.run {
            forwardButton.setOnClickListener { viewModel.nextPost() }
            backButton.setOnClickListener { viewModel.previousPost() }
            retryButton.setOnClickListener { viewModel.requestPosts() }
        }
    }

    companion object {
        private const val PAGE_NUMBER = "page_number"

        fun newInstance(pageNumber: Int): TabFragment {
            val args = Bundle()
            args.putInt(PAGE_NUMBER, pageNumber)
            val fragment = TabFragment()
            fragment.arguments = args
            return fragment
        }
    }
}